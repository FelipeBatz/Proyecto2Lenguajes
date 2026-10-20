/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend;

import java.io.FileReader;
import java.io.Reader;

/**
 *
 * @author felip
 */
public class Main {

    public static void main(String[] args) {

        AnalizadorSintactico2 analizadorSintactico = new AnalizadorSintactico2(obtenerTokens());
        analizadorSintactico.analizar();

    }

    public static ListaEnlazada<Token> obtenerTokens() {

        ListaEnlazada<Token> tokensReconocidos = new ListaEnlazada<>();
        String ruta = "C:/Users/felip/OneDrive/Escritorio/correcto.pz";

        try {
            Reader reader = new FileReader(ruta);
            AnalizadorLexico analizador = new AnalizadorLexico(reader);
            Token tokenActual;
            while ((tokenActual = analizador.yylex()) != null) {
                if (tokenActual.getTipo() == "COMENTARIO") {

                } else {
                    tokensReconocidos.agregarNuevo(tokenActual);

                }
            }
            reader.close();

        } catch (Exception e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
        return tokensReconocidos;
    }
}
