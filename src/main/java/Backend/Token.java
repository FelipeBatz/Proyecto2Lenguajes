/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend;

/**
 *
 * @author felip
 */
public class Token {

    private int noToken;
    private String lexema;
    private String tipoToken;
    private int columna;
    private int fila;

    public Token(int noToken, String lexema, String tipoToken, int fila, int columna) {
        this.noToken = noToken;
        this.lexema = lexema;
        this.tipoToken = tipoToken;
        this.fila = fila;
        this.columna = columna;

    }

    public int getNoToken() {
        return noToken;
    }

    public String getLexema() {
        return lexema;
    }

    public String getTipo() {
        return tipoToken;
    }

    public int getColumna() {
        return columna;
    }

    public int getFila() {
        return fila;
    }

}
