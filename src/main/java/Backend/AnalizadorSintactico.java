
package Backend;
import java.util.ArrayList;
import java.util.List;


public class AnalizadorSintactico {

    // ==========================================
    // CLASE TOKEN
    // ==========================================
    public static class Token {

        private String tipo;
        private String lexema;
        private int fila;
        private int columna;

        public Token(String tipo, String lexema,
                     int fila, int columna) {
            this.tipo = tipo;
            this.lexema = lexema;
            this.fila = fila;
            this.columna = columna;
        }

        public String getTipo() {
            return tipo;
        }

        public String getLexema() {
            return lexema;
        }

        public int getFila() {
            return fila;
        }

        public int getColumna() {
            return columna;
        }
    }

    // ==========================================
    // VARIABLES
    // ==========================================
    private List<Token> tokens;
    private int posicion;
    private final List<String> erroresSintacticos;

    public AnalizadorSintactico() {
        tokens = new ArrayList<>();
        erroresSintacticos = new ArrayList<>();
        posicion = 0;
    }

    // ==========================================
    // MÉTODO PRINCIPAL
    // ==========================================
    public boolean analizar(List<Token> listaTokens) {

        tokens = new ArrayList<>(listaTokens);
        erroresSintacticos.clear();
        posicion = 0;

        if (tokens.isEmpty()
                || !tokens.get(tokens.size() - 1)
                        .getTipo().equals("EOF")) {

            int fila = 1;
            int columna = 1;

            if (!tokens.isEmpty()) {
                Token ultimo = tokens.get(tokens.size() - 1);
                fila = ultimo.getFila();
                columna = ultimo.getColumna()
                        + ultimo.getLexema().length();
            }

            tokens.add(new Token("EOF", "EOF", fila, columna));
        }

        programa();

        return erroresSintacticos.isEmpty();
    }

    // ==========================================
    // <programa>
    // -> <directivas> <agentes> <ejecuciones>
    //    <exportar> EOF
    // ==========================================
    private void programa() {
        directivas();
        agentes();
        ejecuciones();
        exportar();
        consumir("EOF");
    }

    // ==========================================
    // <directivas>
    // -> DIRECTIVA CADENA <directivas> | ε
    // ==========================================
    private void directivas() {

        while (verificar("DIRECTIVA")) {
            consumir("DIRECTIVA");
            consumir("CADENA");
        }
    }

    // ==========================================
    // <agentes>
    // -> <agente> <mas_agentes>
    // ==========================================
    private void agentes() {
        agente();
        masAgentes();
    }

    // ==========================================
    // <mas_agentes>
    // -> <agente> <mas_agentes> | ε
    // ==========================================
    private void masAgentes() {

        while (verificar("AGENTE")) {
            agente();
        }
    }

    // ==========================================
    // <agente>
    // -> AGENTE ID LLAVE_A <contexto>
    //    <variables> <comandos> LLAVE_C
    // ==========================================
    private void agente() {

        consumir("AGENTE");
        consumir("ID");
        consumir("LLAVE_A");

        contexto();
        variables();
        comandos();

        consumir("LLAVE_C");
    }

    // ==========================================
    // <contexto>
    // -> CONTEXTO IGUAL CADENA
    // ==========================================
    private void contexto() {

        consumir("CONTEXTO");
        consumir("IGUAL");
        consumir("CADENA");
    }

    // ==========================================
    // <variables>
    // -> VARIABLE ID IGUAL <expresion> <variables>
    //    | ε
    // ==========================================
    private void variables() {

        while (verificar("VARIABLE")) {
            consumir("VARIABLE");
            consumir("ID");
            consumir("IGUAL");
            expresion();
        }
    }

    // ==========================================
    // <comandos>
    // -> <comando> <mas_comandos>
    // ==========================================
    private void comandos() {
        comando();
        masComandos();
    }

    // ==========================================
    // <mas_comandos>
    // -> <comando> <mas_comandos> | ε
    // ==========================================
    private void masComandos() {

        while (verificar("COMANDO_IA")) {
            comando();
        }
    }

    // ==========================================
    // <comando>
    // -> COMANDO_IA <expresion> <conectores>
    //    FLECHA ID
    // ==========================================
    private void comando() {

        consumir("COMANDO_IA");
        expresion();
        conectores();
        consumir("FLECHA");
        consumir("ID");
    }

    // ==========================================
    // <conectores>
    // -> CONECTOR <expresion> <conectores> | ε
    // ==========================================
    private void conectores() {

        while (verificar("CONECTOR")) {
            consumir("CONECTOR");
            expresion();
        }
    }

    // ==========================================
    // <expresion>
    // -> <termino> <mas_terminos>
    // ==========================================
    private void expresion() {
        termino();
        masTerminos();
    }

    // ==========================================
    // <mas_terminos>
    // -> MAS <termino> <mas_terminos> | ε
    // ==========================================
    private void masTerminos() {

        while (verificar("MAS")) {
            consumir("MAS");
            termino();
        }
    }

    // ==========================================
    // <termino>
    // -> CADENA | ID | NUMERO <unidad> | <cargar>
    // ==========================================
    private void termino() {

        if (verificar("CADENA")) {
            consumir("CADENA");

        } else if (verificar("ID")) {
            consumir("ID");

        } else if (verificar("NUMERO")) {
            consumir("NUMERO");
            unidad();

        } else if (verificar("CARGAR")) {
            cargar();

        } else {
            consumir("CADENA, ID, NUMERO o CARGAR");
        }
    }

    // ==========================================
    // <unidad> -> ID | ε
    // ==========================================
    private void unidad() {

        if (verificar("ID")) {
            consumir("ID");
        }
    }

    // ==========================================
    // <cargar>
    // -> CARGAR PAR_A CADENA PAR_C
    // ==========================================
    private void cargar() {

        consumir("CARGAR");
        consumir("PAR_A");
        consumir("CADENA");
        consumir("PAR_C");
    }

    // ==========================================
    // <ejecuciones>
    // -> EJECUTAR ID <mas_ejecuciones>
    // ==========================================
    private void ejecuciones() {

        consumir("EJECUTAR");
        consumir("ID");
        masEjecuciones();
    }

    // ==========================================
    // <mas_ejecuciones>
    // -> EJECUTAR ID <mas_ejecuciones> | ε
    // ==========================================
    private void masEjecuciones() {

        while (verificar("EJECUTAR")) {
            consumir("EJECUTAR");
            consumir("ID");
        }
    }

    // ==========================================
    // <exportar>
    // -> EXPORTAR ID <mas_ids>
    // ==========================================
    private void exportar() {

        consumir("EXPORTAR");
        consumir("ID");
        masIds();
    }

    // ==========================================
    // <mas_ids>
    // -> COMA ID <mas_ids> | ε
    // ==========================================
    private void masIds() {

        while (verificar("COMA")) {
            consumir("COMA");
            consumir("ID");
        }
    }

    // ==========================================
    // MÉTODOS AUXILIARES
    // ==========================================
    private Token actual() {

        if (posicion >= tokens.size()) {
            return tokens.get(tokens.size() - 1);
        }

        return tokens.get(posicion);
    }

    private boolean verificar(String tipo) {
        return actual().getTipo().equals(tipo);
    }

    private void avanzar() {

        if (posicion < tokens.size() - 1) {
            posicion++;
        }
    }

    private void consumir(String esperado) {

        if (verificar(esperado)) {
            avanzar();
        } else {
            reportarError(esperado);
        }
    }

    // ==========================================
    // REPORTE DE ERRORES
    // ==========================================
    private void reportarError(String esperado) {

        Token encontrado = actual();

        String mensaje = "Error sintáctico: token encontrado '"
                + encontrado.getLexema()
                + "' (" + encontrado.getTipo() + ")"
                + ", se esperaba '" + esperado + "'"
                + ", fila: " + encontrado.getFila()
                + ", columna: " + encontrado.getColumna();

        erroresSintacticos.add(mensaje);
    }

    public List<String> getErroresSintacticos() {
        return new ArrayList<>(erroresSintacticos);
    }

    public void imprimirErrores() {

        if (erroresSintacticos.isEmpty()) {
            System.out.println(
                    "Análisis sintáctico correcto."
            );
        } else {
            for (String error : erroresSintacticos) {
                System.out.println(error);
            }
        }
    }

    public boolean hayErrores() {
        return !erroresSintacticos.isEmpty();
    }
}
