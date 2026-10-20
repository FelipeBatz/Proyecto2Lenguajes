/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend;


import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final List<Token> tokens;
    private int posicion;
    private final List<String> errores;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.posicion = 0;
        this.errores = new ArrayList<>();
    }

    // Token actual
    private Token actual() {
        if (posicion < tokens.size()) {
            return tokens.get(posicion);
        }

        return new Token(0, "EOF", "EOF", 0, 0);
    }

    // Verifica si el token actual es del tipo indicado
    private boolean verificar(String tipo) {
        return actual().getTipo().equals(tipo);
    }

    // Consume un terminal esperado
    private void consumir(String tipo) {
        if (verificar(tipo)) {
            posicion++;
        } else {
            Token token = actual();

            throw new RuntimeException(
                "Error sintáctico en fila "
                + token.getFila()
                + ", columna "
                + token.getColumna()
                + ": se esperaba "
                + tipo
                + " pero se encontró "
                + token.getTipo()
                + " ('"
                + token.getLexema()
                + "')"
            );
        }
    }

    // Inicia el análisis sintáctico
    public boolean analizar() {
        try {
            programa();

            System.out.println(
                "Análisis sintáctico correcto."
            );

            return true;

        } catch (RuntimeException e) {
            errores.add(e.getMessage());

            System.err.println(e.getMessage());

            return false;
        }
    }

    public List<String> getErrores() {
        return errores;
    }

    // <programa> ->
    // <directivas> <agentes> <ejecuciones> <exportar> EOF
    private void programa() {
        directivas();
        agentes();
        ejecuciones();
        exportar();
        consumir("EOF");
    }

    // <directivas> ->
    // DIRECTIVA CADENA <directivas> | ε
    private void directivas() {
        while (verificar("DIRECTIVA")) {
            consumir("DIRECTIVA");
            consumir("CADENA");
        }
    }

    // <agentes> -> <agente> <mas_agentes>
    private void agentes() {
        agente();
        masAgentes();
    }

    // <mas_agentes> -> <agente> <mas_agentes> | ε
    private void masAgentes() {
        while (verificar("AGENTE")) {
            agente();
        }
    }

    // <agente> ->
    // AGENTE ID LLAVE_A <contexto> <variables>
    // <comandos> LLAVE_C
    private void agente() {
        consumir("AGENTE");
        consumir("ID");
        consumir("LLAVE_A");

        contexto();
        variables();
        comandos();

        consumir("LLAVE_C");
    }

    // <contexto> -> CONTEXTO IGUAL CADENA
    private void contexto() {
        consumir("CONTEXTO");
        consumir("IGUAL");
        consumir("CADENA");
    }

    // <variables> ->
    // VARIABLE ID IGUAL <expresion> <variables> | ε
    private void variables() {
        while (verificar("VARIABLE")) {
            consumir("VARIABLE");
            consumir("ID");
            consumir("IGUAL");
            expresion();
        }
    }

    // <comandos> -> <comando> <mas_comandos>
    private void comandos() {
        comando();
        masComandos();
    }

    // <mas_comandos> -> <comando> <mas_comandos> | ε
    private void masComandos() {
        while (verificar("COMANDO_IA")) {
            comando();
        }
    }

    // <comando> ->
    // COMANDO_IA <expresion> <conectores> FLECHA ID
    private void comando() {
        consumir("COMANDO_IA");
        expresion();
        conectores();
        consumir("FLECHA");
        consumir("ID");
    }

    // <conectores> ->
    // CONECTOR <expresion> <conectores> | ε
    private void conectores() {
        while (verificar("CONECTOR")) {
            consumir("CONECTOR");
            expresion();
        }
    }

    // <expresion> -> <termino> <mas_terminos>
    private void expresion() {
        termino();
        masTerminos();
    }

    // <mas_terminos> ->
    // MAS <termino> <mas_terminos> | ε
    private void masTerminos() {
        while (verificar("MAS")) {
            consumir("MAS");
            termino();
        }
    }

    // <termino> ->
    // CADENA | ID | NUMERO <unidad> | <cargar>
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
            throw new RuntimeException(
                "Error sintáctico en fila "
                + actual().getFila()
                + ", columna "
                + actual().getColumna()
                + ": se esperaba CADENA, ID, NUMERO "
                + "o CARGAR."
            );
        }
    }

    // <unidad> -> ID | ε
    private void unidad() {
        if (verificar("ID")) {
            consumir("ID");
        }
    }

    // <cargar> -> CARGAR PAR_A CADENA PAR_C
    private void cargar() {
        consumir("CARGAR");
        consumir("PAR_A");
        consumir("CADENA");
        consumir("PAR_C");
    }

    // <ejecuciones> ->
    // EJECUTAR ID <mas_ejecuciones>
    private void ejecuciones() {
        consumir("EJECUTAR");
        consumir("ID");
        masEjecuciones();
    }

    // <mas_ejecuciones> ->
    // EJECUTAR ID <mas_ejecuciones> | ε
    private void masEjecuciones() {
        while (verificar("EJECUTAR")) {
            consumir("EJECUTAR");
            consumir("ID");
        }
    }

    // <exportar> -> EXPORTAR ID <mas_ids>
    private void exportar() {
        consumir("EXPORTAR");
        consumir("ID");
        masIds();
    }

    // <mas_ids> -> COMA ID <mas_ids> | ε
    private void masIds() {
        while (verificar("COMA")) {
            consumir("COMA");
            consumir("ID");
        }
    }
}
