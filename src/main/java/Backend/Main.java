/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend;



import Frontent.IDE2;
import java.io.FileReader;
import java.io.Reader;

public class Main {

    private static String textoArchivo;
    private static StringBuilder errores;

    public static void main(String[] args) {
        /*
        IDE2 ide = new IDE2();
        ide.setVisible(true);
         */
        ListaEnlazada<Token> listadoTokens = new ListaEnlazada<>();
        ReporteTokens reporteTokens = new ReporteTokens();
        reporteTokens.crearReporte();

        String ruta = "C:/Users/felip/OneDrive/Escritorio/archivoprueba.pz";

        try {
            Reader reader = new FileReader(ruta);
            AnalizadorLexico analizador = new AnalizadorLexico(reader);
            Token tokenActual;
            while ((tokenActual = analizador.yylex()) != null) {

                if (!tokenActual.getTipo().equals("COMENTARIO")) {

                    listadoTokens.agregarNuevo(tokenActual);
                    reporteTokens.agregarDato(tokenActual.getNoToken(), tokenActual.getLexema(), tokenActual.getTipo(), tokenActual.getFila(), tokenActual.getColumna());
                }
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }

        AnalizadorSintactico2 analizador = new AnalizadorSintactico2(listadoTokens);
        analizador.analizar();

    }
}
