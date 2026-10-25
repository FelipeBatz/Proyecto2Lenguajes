/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author felip
 */
public class ReporteTokens {

    private static final String TITULO_LOG = "REPORTE DE TOKENS";
    private static final String TITULO_COLUMNA1 = "No.";
    private static final String TITULO_COLUMNA2 = "Lexema";
    private static final String TITULO_COLUMNA3 = "Tipo";
    private static final String TITULO_COLUMNA4 = "Fila";
    private static final String TITULO_COLUMNA5 = "Columna";

    private FileWriter escribir;

    private String errores = "";

    public void crearReporte() {
        try {
            File pathArchivo = new File("ReporteTokens.html");

            escribir = new FileWriter(pathArchivo);
            escribir.write("<!DOCTYPE html>");
            escribir.write("<html lang=\"es\">");
            escribir.write("<style>");

            escribir.write("* {"
                    + "margin: 0;"
                    + "padding: 0;"
                    + "box-sizing: border-box;"
                    + "}");

            escribir.write("body {"
                    + "font-family: 'Segoe UI', 'Inter', system-ui, sans-serif;"
                    + "background: linear-gradient(135deg, #0f172a, #1e293b);"
                    + "min-height: 100vh;"
                    + "color: #e2e8f0;"
                    + "padding: 50px 20px;"
                    + "}");

            escribir.write(".contenedor {"
                    + "width: 90%;"
                    + "max-width: 1000px;"
                    + "margin: auto;"
                    + "}");

            escribir.write("h1 {"
                    + "text-align: center;"
                    + "font-size: 34px;"
                    + "font-weight: 700;"
                    + "letter-spacing: 1px;"
                    + "margin-bottom: 8px;"
                    + "color: #f8fafc;"
                    + "}");

            escribir.write("h1 span {"
                    + "background: linear-gradient(90deg, #38bdf8, #6366f1);"
                    + "-webkit-background-clip: text;"
                    + "background-clip: text;"
                    + "color: transparent;"
                    + "}");

            escribir.write("h1::after {"
                    + "content: \"\";"
                    + "display: block;"
                    + "width: 120px;"
                    + "height: 4px;"
                    + "border-radius: 4px;"
                    + "margin: 16px auto 40px;"
                    + "background: linear-gradient(90deg, #38bdf8, #6366f1);"
                    + "}");

            escribir.write("table {"
                    + "width: 100%;"
                    + "border-collapse: separate;"
                    + "border-spacing: 0;"
                    + "background-color: #1e293b;"
                    + "border-radius: 14px;"
                    + "overflow: hidden;"
                    + "box-shadow: 0 10px 30px rgba(0, 0, 0, 0.35);"
                    + "border: 1px solid #334155;"
                    + "}");

            escribir.write("thead {"
                    + "background: linear-gradient(90deg, #2563eb, #6366f1);"
                    + "}");

            escribir.write("th {"
                    + "padding: 16px;"
                    + "text-align: center;"
                    + "font-size: 14px;"
                    + "text-transform: uppercase;"
                    + "letter-spacing: 0.5px;"
                    + "font-weight: 600;"
                    + "color: #f8fafc;"
                    + "}");

            escribir.write("td {"
                    + "padding: 14px;"
                    + "text-align: center;"
                    + "font-size: 15px;"
                    + "border-bottom: 1px solid #334155;"
                    + "color: #cbd5e1;"
                    + "}");

            escribir.write("tbody tr {"
                    + "transition: background-color 0.2s ease, transform 0.2s ease;"
                    + "}");

            escribir.write("tbody tr:nth-child(even) {"
                    + "background-color: #24324a;"
                    + "}");

            escribir.write("tbody tr:hover {"
                    + "background-color: #334155;"
                    + "}");

            escribir.write("tbody tr:last-child td {"
                    + "border-bottom: none;"
                    + "}");

            escribir.write("</style>");

            escribir.write("<head>");
            escribir.write("<meta charset=\"UTF-8\">");
            escribir.write("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
            escribir.write("<title>" + TITULO_LOG + "</title>");
            escribir.write("</head>");

            escribir.write("<body>");
            escribir.write("<div class=\"contenedor\">");
            escribir.write("<h1>");
            escribir.write("<span>Listado de</span> Tokens");
            escribir.write("</h1>");

            escribir.write("<table>");
            escribir.write("<thead>");
            escribir.write("<tr>");

            escribir.write("<th>" + TITULO_COLUMNA1 + "</th>");
            escribir.write("<th>" + TITULO_COLUMNA2 + "</th>");
            escribir.write("<th>" + TITULO_COLUMNA3 + "</th>");
            escribir.write("<th>" + TITULO_COLUMNA4 + "</th>");
            escribir.write("<th>" + TITULO_COLUMNA5 + "</th>");
            escribir.write("</tr>");
            escribir.write("</thead>");

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    public void agregarDato(int numero, String lexema, String tipo, int fila, int columna) {

        try {
            escribir.write("<tr>");
            escribir.write("<td>" + numero + "</td>");
            escribir.write("<td>" + lexema + "</td>");
            escribir.write("<td>" + tipo + "</td>");
            escribir.write("<td>" + fila + "</td>");
            escribir.write("<td>" + columna + "</td>");
            escribir.write("</tr>");

        } catch (IOException e) {

            System.out.println("Error al agregar token");

        }
    }

    public void cerrarReporte() {

        try {
            escribir.write("</tbody>");
            escribir.write("</table>");
            escribir.write("</div>");
            escribir.write("</body>");
            escribir.write("</html>");
            escribir.close();
        } catch (IOException e) {
            System.out.println("error al cerrar html");

        }
    }
}
