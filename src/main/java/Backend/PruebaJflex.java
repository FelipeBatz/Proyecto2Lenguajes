/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package Backend;

import java.io.FileReader;
import java.io.Reader;
import Backend.Token;
import Backend.ListaEnlazadaException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author felip
 */
public class PruebaJflex {

    public static void main(String[] args) {

        List<Token> tokens = obtenerTokens();

        Parser parser = new Parser(tokens);

        if (parser.analizar()) {
            System.out.println(
                    "El programa PromptZal es válido."
            );
        } else {
            System.out.println(
                    "El programa contiene errores sintácticos."
            );
        }
    }

    private static List<Token> obtenerTokens() {
        List<Token> tokens = new ArrayList<>();
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
                    tokens.add(tokenActual);
                }

            }
            reader.close();

        } catch (Exception e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
        return tokens;
    }

}
