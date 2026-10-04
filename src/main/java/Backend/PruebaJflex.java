/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package Backend;

import java.io.FileReader;
import java.io.Reader;
import Backend.Token;
import Backend.ListaEnlazadaException;

/**
 *
 * @author felip
 */
public class PruebaJflex {

    public static void main(String[] args) {
        ListaEnlazada<Token> tokensReconocidos = new ListaEnlazada<>();

        String ruta = "C:/Users/felip/OneDrive/Escritorio/correcto.pz";

        try {

            Reader reader = new FileReader(ruta);

            AnalizadorLexico analizador = new AnalizadorLexico(reader);

            Token tokenActual;

            while ((tokenActual = analizador.yylex()) != null) {

                tokensReconocidos.agregarNuevo(tokenActual);
            }

            reader.close();

        } catch (Exception e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }

        for (int i = 0; i < tokensReconocidos.getTamañoLista(); i++) {
            try {
                System.out.printf(
                        "%-6d | %-45s | %-22s | %-8d | %-8d%n",
                        tokensReconocidos.obtenerDatos(i).getNoToken(),
                        tokensReconocidos.obtenerDatos(i).getLexema(),
                        tokensReconocidos.obtenerDatos(i).getTipo(),
                        tokensReconocidos.obtenerDatos(i).getFila(),
                        tokensReconocidos.obtenerDatos(i).getColumna()
                );
            } catch (ListaEnlazadaException ex) {
                System.getLogger(PruebaJflex.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }

            System.out.println(
                    "------------------------------------------------------------------------------------------"
            );

        }

    }
}
