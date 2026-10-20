/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend;

/**
 *
 * @author felip
 */
public class AnalizadorSintactico {

    private ListaEnlazada<Token> listaTokens;
    private ListaEnlazada<String> listaErrores;
    private int posicionListaTokens;

    public AnalizadorSintactico(ListaEnlazada<Token> listaTokens) {
        this.listaTokens = listaTokens;
        listaErrores = new ListaEnlazada<>();
    }

    private Token tokenActual() {
        if (posicionListaTokens < listaTokens.getTamañoLista()) {
            try {
                return listaTokens.obtenerDatos(posicionListaTokens);
            } catch (ListaEnlazadaException ex) {
                System.out.println("indice no existe, error obtener token");
            }
        }
        return new Token(0, "EOF", "EOF", 0, 0);
    }

    private boolean verificarTipoToken(String tipoToken) {
        return tokenActual().getTipo().equals(tipoToken);
    }

    private void esperar(String tipo) {
        if (verificarTipoToken(tipo)) {
            posicionListaTokens++;
        } else {
            Token tokenError = tokenActual();
            System.out.println("Error sintáctico en fila " + tokenError.getFila() + ", columna " + tokenError.getColumna() + ": se esperaba " + tipo+ " pero se encontró " + tokenError.getTipo()+ " " +  tokenError.getLexema());

        }
    }
    
    public void analizar() {
        programa();
        System.out.println("Análisis sintatico correcto");
        
    }
    
    public ListaEnlazada<String> getErrores() {
        return listaErrores;
    }
    
    private void programa() {
        directivas();
        agentes();
        ejecuciones();
        exportar();
        esperar("EOF");
    }
    
    private void directivas() {
        while (verificarTipoToken("DIRECTIVA")) {
            esperar("DIRECTIVA");
            esperar("CADENA");
        }
    }
    
    private void agentes() {
        agente();
        masAgentes();
    }
    
    private void masAgentes() {
        while (verificarTipoToken("AGENTE")) {
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
        while (verificarTipoToken("VARIABLE")) {
            esperar("VARIABLE");
            esperar("ID");
            esperar("IGUAL");
            expresion();
        }
    }
    
    private void comandos() {
        comando();
        masComandos();
    }
    
    private void masComandos() {
        while (verificarTipoToken("COMANDO_IA")) {
            comando();
        }
    }
    
    private void comando() {
        esperar("COMANDO_IA");
        expresion();
        conectores();
        esperar("FLECHA");
        esperar("ID");
    }
    
    private void conectores() {
        while (verificarTipoToken("CONECTOR")) {
            esperar("CONECTOR");
            expresion();
        }
    }
    
    private void expresion() {
        termino();
        masTerminos();
    }
    
    private void masTerminos() {
        while (verificarTipoToken("MAS")) {
            esperar("MAS");
            termino();
        }
    }
    
    private void termino() {
        if (verificarTipoToken("CADENA")) {
            esperar("CADENA");

        } else if (verificarTipoToken("ID")) {
            esperar("ID");

        } else if (verificarTipoToken("NUMERO")) {
            esperar("NUMERO");
            unidad();

        } else if (verificarTipoToken("CARGAR")) {
            cargar();

        } else {
            throw new RuntimeException(
                "Error sintáctico en fila "
                + tokenActual().getFila()
                + ", columna "
                + tokenActual().getColumna()
                + ": se esperaba CADENA, ID, NUMERO "
                + "o CARGAR."
            );
        }
    }
    
    private void unidad() {
        if (verificarTipoToken("ID")) {
            esperar("ID");
        }
    }
    
    private void cargar() {
        esperar("CARGAR");
        esperar("PAR_A");
        esperar("CADENA");
        esperar("PAR_C");
    }
    
    private void ejecuciones() {
        esperar("EJECUTAR");
        esperar("ID");
        masEjecuciones();
    }
    
    private void masEjecuciones() {
        while (verificarTipoToken("EJECUTAR")) {
            esperar("EJECUTAR");
            esperar("ID");
        }
    }
    
    private void exportar() {
        esperar("EXPORTAR");
        esperar("ID");
        masIds();
    }
    
    private void masIds() {
        while (verificarTipoToken("COMA")) {
            esperar("COMA");
            esperar("ID");
        }
    }

}
