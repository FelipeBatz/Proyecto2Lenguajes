package Backend;

public class AnalizadorSintactico2 {

    private ListaEnlazada<Token> listaTokens;
    private ListaEnlazada<String> listaErrores;
    private int posicionListaTokens;
    private NodoAST nodoInicial;

    private final String[] TOKENS_SINCRONIZACION = {
        "AGENTE",
        "LLAVE_C",
        "COMANDO_IA",
        "VARIABLE",
        "EJECUTAR",
        "EXPORTAR",
        "EOF"
    };

    private NodoAST raizAST;

    public AnalizadorSintactico2(
            ListaEnlazada<Token> listaTokens) {
        nodoInicial = nuevoNodo("Programa", "", "", 1);
        this.listaTokens = listaTokens;
        this.listaErrores = new ListaEnlazada<>();
        this.posicionListaTokens = 0;
        this.raizAST = null;
    }

    // =====================================================
    // MÉTODOS GENERALES
    // =====================================================
    private Token tokenActual() {

        if (posicionListaTokens < listaTokens.getTamañoLista()) {
            try {
                return listaTokens.obtenerDatos(
                        posicionListaTokens
                );
            } catch (ListaEnlazadaException ex) {
                System.out.println(
                        "Error al obtener el token actual."
                );
            }
        }

        return new Token(0, "EOF", "EOF", 0, 0);
    }

    private boolean verificarToken(String tipo) {
        return tokenActual().getTipo().equals(tipo);
    }

    private void avanzar() {
        if (posicionListaTokens < listaTokens.getTamañoLista()
                && !verificarToken("EOF")) {
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
    }

    /*
     * Espera un token.
     *
     * Si lo encuentra, lo consume y lo devuelve.
     * Si no lo encuentra, informa el error y busca
     * el token esperado sin eliminar los tokens
     * de sincronización.
     */
    private String esperar(String tipoEsperado) {

        if (verificarToken(tipoEsperado)) {
            String lexema = tokenActual().getLexema();
            avanzar();
            return lexema;
        }

        reportarError(tokenActual(), tipoEsperado);

        while (!verificarToken("EOF")
                && !verificarToken(tipoEsperado)
                && !esTokenSincronizacion(
                        tokenActual().getTipo())) {

            avanzar();
        }

        if (verificarToken(tipoEsperado)) {
            Token token = tokenActual();
            avanzar();
            return token.getLexema();
        }

        return "";

    }

    private NodoAST nuevoNodo(String tipo, String lexema, String valor, int fila) {
        return new NodoAST(tipo, lexema, valor, fila);
    }

    private void agregarHijo(
            NodoAST padre,
            NodoAST hijo) {

        if (padre != null && hijo != null) {
            padre.agregarHijo(hijo);
        }
    }

    // =====================================================
    // ANÁLISIS PRINCIPAL
    // =====================================================
    public NodoAST analizar() {

        posicionListaTokens = 0;
        listaErrores = new ListaEnlazada<>();
        raizAST = null;

        raizAST = programa();

        if (raizAST != null) {
            try {
                ReporteASTHtml reporte = new ReporteASTHtml();

                reporte.generar(
                        raizAST,
                        "reporteAST.html"
                );

                System.out.println(
                        "Reporte AST generado: reporteAST.html"
                );

            } catch (java.io.IOException ex) {
                System.out.println(
                        "Error al generar el HTML: " + ex.getMessage()
                );
            }
        }

        System.out.println("====== ERRORES ======");

        if (listaErrores.getTamañoLista() > 0) {

            for (int i = 0;
                    i < listaErrores.getTamañoLista();
                    i++) {

                try {
                    System.out.println(
                            listaErrores.obtenerDatos(i)
                    );
                } catch (ListaEnlazadaException ex) {
                    System.out.println(
                            "Error al mostrar errores."
                    );
                }
            }

        } else {
            System.out.println(
                    "No se encontraron errores sintácticos."
            );
        }

        System.out.println("Análisis finalizado.");

        return raizAST;
    }

    public NodoAST getRaizAST() {
        return raizAST;
    }

    public ListaEnlazada<String> getListaErrores() {
        return listaErrores;
    }

    // =====================================================
    // <programa>
    // =====================================================
    private NodoAST programa() {

        directivas();
        agentes();
        agregarHijo(nodoInicial, ejecuciones());
        agregarHijo(nodoInicial, exportar());
        esperar("EOF");
        return nodoInicial;
    }

    // =====================================================
    // <directivas>
    // =====================================================
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
        int filaActual = tokenActual().getFila();
        String lexema = esperar("AGENTE");
        String id = esperar("ID");
        NodoAST nodo = nuevoNodo(lexema, id, id, filaActual);
        agregarHijo(nodoInicial, nodo);
        esperar("LLAVE_A");
        contexto(nodo);
        variables(nodo);
        comandos(nodo);
        esperar("LLAVE_C");

    }

    // =====================================================
    // <contexto>
    // =====================================================
    private void contexto(NodoAST nodopadre) {
        int filaActual = tokenActual().getFila();
        String lexema = esperar("CONTEXTO");
        esperar("IGUAL");
        String cadena = esperar("CADENA");
        NodoAST nodo = nuevoNodo(lexema, cadena, cadena, filaActual);
        agregarHijo(nodopadre, nodo);
    }

    // =====================================================
    // <variables>
    // =====================================================
    private void variables(NodoAST nodoPadre) {

        while (verificarToken("VARIABLE")) {
            int filaActual = tokenActual().getFila();
            int posicionAnterior = posicionListaTokens;
            String lexema = esperar("VARIABLE");
            String id = esperar("ID");
            esperar("IGUAL");
            expresion();
            variables(nodoPadre);
            NodoAST nodo = nuevoNodo(lexema, id, id, filaActual);
            agregarHijo(nodoPadre, nodo);

            if (posicionAnterior == posicionListaTokens) {
                avanzar();
            }
        }

    }

    private void comandos(NodoAST nodoPadre) {
        comando(nodoPadre);
        mas_comandos(nodoPadre);

    }

    private void mas_comandos(NodoAST nodoPadre) {
        while (verificarToken("COMANDO_IA")) {
            comando(nodoPadre);
        }
    }



    private void comando(NodoAST nodoPadre) {
        int filaActual = tokenActual().getFila();
        int posicionAnterior = posicionListaTokens;
        String lexema = esperar("COMANDO_IA");
        expresion();
        conectores();
        esperar("FLECHA");
        String id = esperar("ID");
        NodoAST nodo = nuevoNodo(lexema, id, id, filaActual);
        agregarHijo(nodoPadre, nodo);
        if (posicionAnterior == posicionListaTokens) {
            avanzar();
        }
    }

    private void conectores() {
        while (verificarToken("CONECTOR")) {
            esperar("CONECTOR");
            expresion(); 
        }
    }

 
    public NodoAST expresion() {

        NodoAST nodo = nuevoNodo("Expresion", "", "", 1);

        agregarHijo(nodo, termino());

        while (verificarToken("MAS")) {

            int posicionAnterior = posicionListaTokens;

            String operador = esperar("MAS");

            NodoAST nodoOperador = nuevoNodo(
                    "Operador", "",
                    operador, 1
            );

            agregarHijo(
                    nodoOperador,
                    termino()
            );

            agregarHijo(nodo, nodoOperador);

            if (posicionAnterior == posicionListaTokens) {
                avanzar();
            }
        }

        return nodo;
    }

    public NodoAST termino() {

        if (verificarToken("CADENA")) {

            String token = esperar("CADENA");

            return nuevoNodo(
                    "Cadena", "",
                    token, 1
            );

        } else if (verificarToken("ID")) {

            String token = esperar("ID");

            return nuevoNodo(
                    "Identificador", "",
                    token, 1
            );

        } else if (verificarToken("NUMERO")) {

            String token = esperar("NUMERO");

            NodoAST nodo = nuevoNodo("",
                    "Numero",
                    token, 1
            );

            agregarHijo(nodo, unidad());

            return nodo;

        } else if (verificarToken("CARGAR")) {

            return cargar();
        }

        reportarError(
                tokenActual(),
                "CADENA, ID, NUMERO o CARGAR"
        );

        recuperarTermino();

        return nuevoNodo("ErrorExpresion", "", "", 1);
    }

    // =====================================================
    // <unidad>
    // =====================================================
    public NodoAST unidad() {

        if (verificarToken("ID")) {

            String token = esperar("ID");

            return nuevoNodo("",
                    "Unidad",
                    token, 1
            );
        }

        return nuevoNodo("Unidad", "", "", 1);
    }

    // =====================================================
    // <cargar>
    // =====================================================
    public NodoAST cargar() {

        esperar("CARGAR");

        NodoAST nodo = nuevoNodo("Cargar", "", "CARGAR", 1);

        esperar("PAR_A");

        String ruta = esperar("CADENA");

        agregarHijo(
                nodo,
                nuevoNodo("Ruta", "", ruta, 1)
        );

        esperar("PAR_C");

        return nodo;
    }

    // =====================================================
    // <ejecuciones>
    // =====================================================
    public NodoAST ejecuciones() {

        NodoAST nodo = nuevoNodo("Ejecuciones", "", "", 1);

        if (verificarToken("EJECUTAR")) {
            agregarHijo(nodo, ejecucion());
        } else {
            reportarError(tokenActual(), "EJECUTAR");
        }

        while (verificarToken("EJECUTAR")) {

            int posicionAnterior = posicionListaTokens;

            agregarHijo(nodo, ejecucion());

            if (posicionAnterior == posicionListaTokens) {
                avanzar();
            }
        }

        return nodo;
    }

    private NodoAST ejecucion() {

        esperar("EJECUTAR");

        String identificador = esperar("ID");

        return nuevoNodo(
                "Ejecucion",
                identificador, "", 1
        );
    }

    // =====================================================
    // <exportar>
    // =====================================================
    public NodoAST exportar() {

        NodoAST nodo = nuevoNodo("Exportar", "", "", 1);

        esperar("EXPORTAR");

        String identificador = esperar("ID");

        agregarHijo(
                nodo,
                nuevoNodo(
                        "Identificador", "",
                        identificador, 1
                )
        );

        while (verificarToken("COMA")) {

            int posicionAnterior = posicionListaTokens;

            esperar("COMA");

            String id = esperar("ID");

            agregarHijo(
                    nodo,
                    nuevoNodo(
                            "Identificador", "",
                            id, 1
                    )
            );

            if (posicionAnterior == posicionListaTokens) {
                avanzar();
            }
        }

        return nodo;
    }

    // =====================================================
    // RECUPERACIÓN DE ERRORES EN EXPRESIONES
    // =====================================================
    private void recuperarTermino() {

        while (!verificarToken("EOF")
                && !verificarToken("MAS")
                && !verificarToken("FLECHA")
                && !verificarToken("CONECTOR")
                && !verificarToken("LLAVE_C")
                && !verificarToken("VARIABLE")
                && !verificarToken("COMANDO_IA")
                && !verificarToken("EJECUTAR")
                && !verificarToken("EXPORTAR")) {

            avanzar();
        }
    }
}
