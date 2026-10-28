/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend.Reportes;



import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReporteErroresLexicos {

    private static class ErrorLexico {
        String lexema;
        String tipo;
        int fila;
        int columna;

        public ErrorLexico(
                String lexema,
                String tipo,
                int fila,
                int columna) {

            this.lexema = lexema;
            this.tipo = tipo;
            this.fila = fila;
            this.columna = columna;
        }
    }

    private final List<ErrorLexico> errores = new ArrayList<>();

    // Agregar un error léxico al reporte
    public void agregarError(
            String lexema,
            String tipo,
            int fila,
            int columna) {

        errores.add(new ErrorLexico(
                lexema, tipo, fila, columna));
    }

    // Generar el archivo HTML
    public void generarReporte(String rutaArchivo) {

        try (BufferedWriter bw =
                new BufferedWriter(new FileWriter(rutaArchivo))) {

            bw.write("""
                <!DOCTYPE html>
                <html lang="es">
                <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Reporte de errores léxicos</title>

                <style>
                * {
                    box-sizing: border-box;
                }

                body {
                    margin: 0;
                    padding: 35px;
                    font-family: 'Segoe UI', Arial, sans-serif;
                    background: #f1f5f9;
                    color: #1e293b;
                }

                .contenedor {
                    max-width: 1100px;
                    margin: auto;
                    background: white;
                    border-radius: 16px;
                    overflow: hidden;
                    box-shadow: 0 10px 35px rgba(15,23,42,.08);
                }

                header {
                    background: #172033;
                    color: white;
                    padding: 25px 30px;
                }

                header h1 {
                    margin: 0 0 8px;
                    font-size: 25px;
                }

                header p {
                    color: #cbd5e1;
                    margin: 0;
                }

                main {
                    padding: 25px 30px;
                }

                .barra {
                    display: flex;
                    align-items: center;
                    justify-content: space-between;
                    gap: 12px;
                    margin-bottom: 20px;
                }

                .contador {
                    background: #ffedd5;
                    color: #c2410c;
                    padding: 8px 14px;
                    border-radius: 20px;
                    font-size: 13px;
                    font-weight: 600;
                    white-space: nowrap;
                }

                .tabla-contenedor {
                    overflow-x: auto;
                    border: 1px solid #e2e8f0;
                    border-radius: 10px;
                }

                table {
                    width: 100%;
                    border-collapse: collapse;
                    text-align: left;
                }

                thead {
                    background: #e8eef5;
                }

                th {
                    padding: 16px;
                    color: #334155;
                    font-size: 13px;
                    white-space: nowrap;
                }

                td {
                    padding: 15px 16px;
                    border-top: 1px solid #e2e8f0;
                    font-size: 14px;
                }

                tbody tr:nth-child(even) {
                    background: #f8fafc;
                }

                tbody tr:hover {
                    background: #eff6ff;
                }

                .lexema {
                    color: #7c3aed;
                    font-family: Consolas, monospace;
                    font-weight: 600;
                    overflow-wrap: anywhere;
                }

                .tipo {
                    color: #c2410c;
                    font-weight: 600;
                }

                .vacio {
                    text-align: center;
                    padding: 35px;
                    color: #64748b;
                }

                footer {
                    padding: 18px 30px;
                    border-top: 1px solid #e2e8f0;
                    color: #64748b;
                    font-size: 12px;
                }

                @media (max-width: 600px) {
                    body {
                        padding: 12px;
                    }

                    main {
                        padding: 18px;
                    }

                    header {
                        padding: 22px;
                    }
                }
                </style>
                </head>

                <body>
                <div class="contenedor">

                <header>
                    <h1>Reporte de errores léxicos</h1>
                    <p>Caracteres y lexemas no reconocidos durante el análisis</p>
                </header>

                <main>
                    <div class="barra">
                        <h2>Listado de errores</h2>
                        <span class="contador">
                """);

            bw.write(errores.size()
                    + (errores.size() == 1
                    ? " error léxico"
                    : " errores léxicos"));

            bw.write("""
                        </span>
                    </div>

                    <div class="tabla-contenedor">
                    <table>
                    <thead>
                    <tr>
                        <th>No.</th>
                        <th>Lexema</th>
                        <th>Tipo</th>
                        <th>Fila</th>
                        <th>Columna</th>
                    </tr>
                    </thead>
                    <tbody>
                """);

            if (errores.isEmpty()) {
                bw.write("""
                    <tr>
                        <td colspan="5" class="vacio">
                            No hay errores léxicos registrados.
                        </td>
                    </tr>
                    """);
            } else {
                int numero = 1;

                for (ErrorLexico error : errores) {
                    bw.write("<tr>");

                    bw.write("<td>" + numero++ + "</td>");

                    bw.write("<td class='lexema'>"
                            + escaparHTML(error.lexema)
                            + "</td>");

                    bw.write("<td class='tipo'>"
                            + escaparHTML(error.tipo)
                            + "</td>");

                    bw.write("<td>" + error.fila + "</td>");
                    bw.write("<td>" + error.columna + "</td>");

                    bw.write("</tr>\n");
                }
            }

            bw.write("""
                    </tbody>
                    </table>
                    </div>
                </main>

                <footer>
                    Reporte generado por el analizador léxico.
                </footer>

                </div>
                </body>
                </html>
                """);

            System.out.println(
                    "Reporte generado correctamente: " + rutaArchivo);

        } catch (IOException e) {
            System.err.println(
                    "Error al generar el reporte: " + e.getMessage());
        }
    }

    // Escapar caracteres especiales para evitar HTML incorrecto
    private String escaparHTML(String texto) {
        if (texto == null) {
            return "";
        }

        return texto
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

 
    
}
