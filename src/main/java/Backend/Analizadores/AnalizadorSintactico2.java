package Backend.Analizadores;

import Backend.GenerarPromptzal;
import Backend.ListaEnlazadaGenerica.ListaEnlazada;
import Backend.ListaEnlazadaGenerica.ListaEnlazadaException;
import Backend.AST.NodoAST;
import Backend.AST.ReporteASTHtml;
import Backend.Token;
import Backend.Reportes.ReporteErroresSintacticos;
import Backend.Simbolo;

public class AnalizadorSintactico2 {

    private ListaEnlazada<Token> listaTokens;
    private ListaEnlazada<String> listaErrores;
    private int posicionListaTokens;
    private NodoAST nodoInicial;
    private String promptGenerado;
    private final String[] TOKENS_SINCRONIZACION = {"AGENTE", "LLAVE_C", "COMANDO_IA", "VARIABLE", "EJECUTAR", "EXPORTAR", "EOF"};
    private NodoAST raizAST;
    private ReporteErroresSintacticos erroresSintacticos;
    private ListaEnlazada<Simbolo> listaSimbolos;
    private boolean existeAgente;
    private String agenteActual;
    private ListaEnlazada<NodoAST> listadoNodos;

    public AnalizadorSintactico2(ListaEnlazada<Token> listaTokens) {
        nodoInicial = nuevoNodo("Programa", "", "", 1);
        this.listaTokens = listaTokens;
        this.listaErrores = new ListaEnlazada<>();
        this.posicionListaTokens = 0;
        this.raizAST = null;
        erroresSintacticos = new ReporteErroresSintacticos();
        listaSimbolos = new ListaEnlazada<>();
    }

    private Token tokenActual() {
        if (posicionListaTokens < listaTokens.getTamañoLista()) {
            try {
                return listaTokens.obtenerDatos(posicionListaTokens);
            } catch (ListaEnlazadaException ex) {
                System.out.println("Error al obtener el token actual.");
            }
        }

        return new Token(0, "EOF", "EOF", 0, 0);
    }

    private boolean verificarToken(String tipo) {
        return tokenActual().getTipo().equals(tipo);
    }

    private void avanzar() {
        if (posicionListaTokens < listaTokens.getTamañoLista() && !verificarToken("EOF")) {
            posicionListaTokens++;
        }
    }

    private boolean esTokenSincronizacion(String tipo) {
        for (String token : TOKENS_SINCRONIZACION) {
            if (token.equals(tipo)) {
                return true;
            }
        }
        return false;
    }

    private void reportarError(Token token, String esperado) {

        String error
                = "Error sintáctico en fila "
                + token.getFila()
                + ", columna "
                + token.getColumna()
                + ": se esperaba "
                + esperado
                + ", pero se encontró "
                + token.getTipo()
                + " ("
                + token.getLexema()
                + ").";

        listaErrores.agregarNuevo(error);
        erroresSintacticos.agregarError(esperado, token.getTipo(), token.getFila(), token.getColumna());
    }

    private String esperar(String tipoEsperado) {

        if (verificarToken(tipoEsperado)) {
            String lexema = tokenActual().getLexema();
            avanzar();
            return lexema;
        }

        reportarError(tokenActual(), tipoEsperado);

        while (!verificarToken("EOF") && !verificarToken(tipoEsperado) && !esTokenSincronizacion(tokenActual().getTipo())) {
            avanzar();

            if (verificarToken("AGENTE")) {
                existeAgente = true;
            }
        }

        if (verificarToken(tipoEsperado)) {
            Token token = tokenActual();
            avanzar();
            return token.getLexema();
        }

        return "";

    }

    

    public void analizar() {
        ReporteASTHtml reporte = new ReporteASTHtml();
        posicionListaTokens = 0;
        listaErrores = new ListaEnlazada<>();
        raizAST = null;

        raizAST = programa();
        if (raizAST != null) {
            try {

                reporte.generar(raizAST, "reporteAST.html");

            } catch (java.io.IOException ex) {
                System.out.println("Error al generar el HTML: " + ex.getMessage());
            }
        }

        GenerarPromptzal promp = new GenerarPromptzal(reporte.getNodos());
        listadoNodos = reporte.getNodos();
        promptGenerado = promp.generar();
        erroresSintacticos.generarReporte("ErroresSintacticos.html");

        

    }
    
    public ListaEnlazada<NodoAST> getListadoNodos() {
        return listadoNodos;
    }

    public String getPromptGenerado() {
        return promptGenerado;
    }

    public NodoAST getRaizAST() {
        return raizAST;
    }

    public ListaEnlazada<String> getListaErrores() {
        return listaErrores;
    }

    private NodoAST programa() {
        directivas();
        agentes();
        ejecuciones();
        exportar();
        esperar("EOF");
        return nodoInicial;
    }

    private void directivas() {
        while (verificarToken("DIRECTIVA")) {
            int posicionAnterior = posicionListaTokens;
            int filaActual = tokenActual().getFila();
            String lexema = esperar("DIRECTIVA");
            String cadena = esperar("CADENA");
            NodoAST nodo = nuevoNodo("DIRECTIVA", lexema, cadena, filaActual);
            agregarHijo(nodoInicial, nodo);
            if (posicionAnterior == posicionListaTokens) {
                avanzar();
            }
        }

    }

    private void agentes() {
        agente();
        mas_agentes();
    }

    private void mas_agentes() {
        while (verificarToken("AGENTE")) {
            agente();
            mas_agentes();
        }
    }

    private void agente() {
        if (verificarToken("AGENTE")) {
            existeAgente = true;
        }

        String lexema = esperar("AGENTE");
        String id = esperar("ID");
        agenteActual = id;
        NodoAST nodo = nuevoNodo("", lexema, id, tokenActual().getFila());
        int filaActual = tokenActual().getFila();
        if (existeAgente) {
            Simbolo simbolo = new Simbolo(id, "AGENTE", "Todo el programa", filaActual);
            listaSimbolos.agregarNuevo(simbolo);
        }
        agregarHijo(nodoInicial, nodo);
        esperar("LLAVE_A");
        contexto(nodo);
        variables(nodo);
        comandos(nodo);
        esperar("LLAVE_C");

        existeAgente = false;

    }

    private void contexto(NodoAST nodopadre) {
        int filaActual = tokenActual().getFila();
        esperar("CONTEXTO");
        esperar("IGUAL");
        String cadena = esperar("CADENA");
        NodoAST nodo = nuevoNodo("", "CONTEXTO", cadena, filaActual);
        agregarHijo(nodopadre, nodo);
    }

    private void variables(NodoAST nodoPadre) {
        while (verificarToken("VARIABLE")) {
            int filaActual = tokenActual().getFila();
            int posicionAnterior = posicionListaTokens;
            String variable = esperar("VARIABLE");
            String id = esperar("ID");
            esperar("IGUAL");
            String expresion = expresion();
            variables(nodoPadre);
            NodoAST nodo = nuevoNodo("VARIABLE", id, expresion, filaActual);
            agregarHijo(nodoPadre, nodo);
            Simbolo simbolo = new Simbolo(id, "VARIABLE", "Dentro de " + agenteActual, filaActual);
            listaSimbolos.agregarNuevo(simbolo);

            if (posicionAnterior == posicionListaTokens) {
                avanzar();
            }
        }
    }

    private void comandos(NodoAST nodoPadre) {
        comando(nodoPadre, agenteActual);
        mas_comandos(nodoPadre);

    }

    private void mas_comandos(NodoAST nodoPadre) {
        while (verificarToken("COMANDO_IA")) {
            comando(nodoPadre, agenteActual);
        }
    }

    private void comando(NodoAST nodoPadre, String agente) {
        int filaActual = tokenActual().getFila();
        int posicionAnterior = posicionListaTokens;
        String tipoComando = esperar("COMANDO_IA");
        String expresion = expresion();
        String conector = conectores();
        esperar("FLECHA");
        String id = esperar("ID");
        Simbolo simbolo = new Simbolo(id, "VARIABLE", "Dentro de " + agenteActual, filaActual);
        listaSimbolos.agregarNuevo(simbolo);

        NodoAST.PreguntarNodo preguntarNodo = new NodoAST.PreguntarNodo(tipoComando, expresion, conector, id, filaActual);
        agregarHijo(nodoPadre, preguntarNodo);
        if (posicionAnterior == posicionListaTokens) {
            avanzar();
        }
    }

    private String conectores() {
        String expresion = "";
        while (verificarToken("CONECTOR")) {
            esperar("CONECTOR");
            expresion = expresion();
        }
        return expresion;
    }

    private String expresion() {
        StringBuilder terminos = new StringBuilder();
        String termino = termino();
        String masTeminos = mas_terminos();
        terminos.append(termino + masTeminos);
        return termino.toString();
    }

    private String mas_terminos() {
        String mas = "";
        String termino = "";
        while (verificarToken("MAS")) {
            mas = esperar("MAS");
            termino = termino();
        }
        return mas + termino;

    }

    public String termino() {
        if (verificarToken("CADENA")) {
            String cadena = esperar("CADENA");
            return cadena;

        } else if (verificarToken("ID")) {
            String id = esperar("ID");
            return id;

        } else if (verificarToken("NUMERO")) {
            String numero = esperar("NUMERO");
            String id = unidad();

            return numero + " " + id;

        } else if (verificarToken("CARGAR")) {
            String cargar = cargar();
            return cargar;
        }

        reportarError(tokenActual(), "CADENA, ID, NUMERO o CARGAR");
        recuperarTermino();
        return "";

    }

    public String unidad() {
        if (verificarToken("ID")) {
            String id = esperar("ID");
            return id;
        } else {
            reportarError(tokenActual(), "ID");
            return "";
        }
    }

    public String cargar() {
        esperar("CARGAR");
        esperar("PAR_A");
        String cadena = esperar("CADENA");
        esperar("PAR_C");
        return cadena;
    }

    public void ejecuciones() {
        int filaActual = tokenActual().getFila();
        esperar("EJECUTAR");
        String id = esperar("ID");
        NodoAST nodo = nuevoNodo("EJECUTAR", id, id, filaActual);
        agregarHijo(nodoInicial, nodo);
        mas_ejecuciones();

    }

    private void mas_ejecuciones() {

        while (verificarToken("EJECUTAR")) {
            int filaActual = tokenActual().getFila();
            esperar("EJECUTAR");
            String id = esperar("ID");
            NodoAST nodo = nuevoNodo("EJECUTAR", id, id, filaActual);
            agregarHijo(nodoInicial, nodo);
        }
    }

    public void exportar() {
        int filaActual = tokenActual().getFila();
        esperar("EXPORTAR");
        String identificador = esperar("ID");
        String masIDS = mas_ids();
        NodoAST nodo = nuevoNodo("EXPORTAR", identificador + masIDS, identificador + masIDS, filaActual);
        agregarHijo(nodoInicial, nodo);
    }

    public String mas_ids() {
        StringBuilder masIDS = new StringBuilder();
        while (verificarToken("COMA")) {
            int posicionAnterior = posicionListaTokens;
            String coma = esperar("COMA");
            String id = esperar("ID");
            masIDS.append(coma + id);
            if (posicionAnterior == posicionListaTokens) {
                avanzar();
            }
        }
        return masIDS.toString();
    }

    public ListaEnlazada<Simbolo> getListadoSimbolos() {
        return listaSimbolos;
    }
    
    private NodoAST nuevoNodo(String tipo, String lexema, String valor, int fila) {
        return new NodoAST(tipo, lexema, valor, fila);
    }

    private void agregarHijo(NodoAST padre, NodoAST hijo) {
        if (padre != null && hijo != null) {
            padre.agregarHijo(hijo);
        }
    }

    private void recuperarTermino() {
        while (!verificarToken("EOF") && !verificarToken("MAS") && !verificarToken("FLECHA") && !verificarToken("CONECTOR")
                && !verificarToken("LLAVE_C")
                && !verificarToken("VARIABLE")
                && !verificarToken("COMANDO_IA")
                && !verificarToken("EJECUTAR")
                && !verificarToken("EXPORTAR")) {
            avanzar();
        }
    }
}
