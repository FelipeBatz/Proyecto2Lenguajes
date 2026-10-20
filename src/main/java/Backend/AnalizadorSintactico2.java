/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend;

/**
 *
 * @author felip
 */
public class AnalizadorSintactico2 {

    private ListaEnlazada<Token> listaTokens;
    private ListaEnlazada<String> listaErrores;
    private int posicionListaTokens;

    public AnalizadorSintactico2(ListaEnlazada<Token> listaTokens) {
        this.listaTokens = listaTokens;
        listaErrores = new ListaEnlazada<>();
    }

    private void reportarError(Token tokenActual, String tipo) {
        String error = ("Se esperaba: " + tipo
                + " , pero llego: " + tokenActual.getTipo()
                + " , columna: " + tokenActual.getColumna()
                + " , Fila: " + tokenActual.getFila());

        listaErrores.agregarNuevo(error);
    }

    private Token tokenActual() {
        if (posicionListaTokens < listaTokens.getTamañoLista()) {
            try {
                return listaTokens.obtenerDatos(posicionListaTokens);
            } catch (ListaEnlazadaException ex) {
                System.out.println("Error en obtener token actual, indice no existe");
            }
        }
        return new Token(0, "EOF", "EOF", 0, 0);
    }

    private boolean verificarToken(String tipo) {
        return tokenActual().getTipo().equals(tipo);
    }

    public void analizar() {
        programa();
        if(listaErrores.getTamañoLista() > 0 ){
            for (int i = 0; i < listaErrores.getTamañoLista(); i++) {
                try {
                    System.out.println(listaErrores.obtenerDatos(i));
                } catch (ListaEnlazadaException ex) {
                    System.out.println("Error al mostar errores");
                }
                
            }
        }else {
            System.out.println("No se encontraron errores");
        }
        System.out.println("Analisis finalizado");
        
    }

    private void esperar(String tipo) {
        if (verificarToken(tipo)) {
            posicionListaTokens++;
        } else {
            Token tokenError = tokenActual();
            reportarError(tokenError, tipo);
        }

    }

    private void programa() {
        directivas();
        agentes();
        ejecuciones();
        exportar();
        esperar("EOF");

    }

    private void directivas() {
        while (verificarToken("DIRECTIVA")) {
            esperar("DIRECTIVA");
            esperar("CADENA");
        }
    }

    private void agentes() {
        agente();
        mas_agentes();
    }

    private void mas_agentes() {
        while (verificarToken("AGENTE")) {
            agente();
        }
    }

    private void agente() {
        esperar("AGENTE");
        esperar("ID");
        esperar("LLAVE_A");
        contexto();
        variables();
        comandos();
        esperar("LLAVE_C");
    }

    private void contexto() {
        esperar("CONTEXTO");
        esperar("IGUAL");
        esperar("CADENA");
    }

    private void variables() {
        while (verificarToken("VARIABLE")) {
            esperar("VARIABLE");
            esperar("ID");
            esperar("IGUAL");
            expresion();
        }
    }

    private void comandos() {
        comando();
        mas_comandos();
    }

    private void mas_comandos() {
        while (verificarToken("COMANDO_IA")) {
            comando();
        }
    }

    public void comando() {
        esperar("COMANDO_IA");
        expresion();
        conectores();
        esperar("FLECHA");
        esperar("ID");
    }

    public void conectores() {
        while (verificarToken("CONECTOR")) {
            esperar("CONECTOR");
            expresion();
        }
    }

    public void expresion() {
        termino();
        mas_terminos();
    }

    public void mas_terminos() {
        while (verificarToken("MAS")) {
            esperar("MAS");
            termino();
        }
    }

    public void termino() {
        if (verificarToken("CADENA")) {
            esperar("CADENA");
        } else if (verificarToken("ID")) {
            esperar("ID");
        } else if (verificarToken("NUMERO")) {
            esperar("NUMERO");
            unidad();
        } else if (verificarToken("CARGAR")) {
            cargar();
        } else {
            reportarError(tokenActual(), "CADENA, ID, NUMERO o CARGAR");
        }
    }

    public void unidad() {
        if (verificarToken("ID")) {
            esperar("ID");
        }
    }

    public void cargar() {
        esperar("CARGAR");
        esperar("PAR_A");
        esperar("CADENA");
        esperar("PAR_C");
    }

    public void ejecuciones() {
        esperar("EJECUTAR");
        esperar("ID");
        mas_ejecuciones();
    }

    public void mas_ejecuciones() {
        while (verificarToken("EJECUTAR")) {
            esperar("EJECUTAR");
            esperar("ID");
        }
    }

    public void exportar() {
        esperar("EXPORTAR");
        esperar("ID");
        mas_ids();
    }

    public void mas_ids() {
        while (verificarToken("COMA")) {
            esperar("COMA");
            esperar("ID");
        }
    }
}
