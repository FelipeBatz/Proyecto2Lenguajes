package Backend.Reportes;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReporteErroresSemanticos {

    private final List<String> errores = new ArrayList<>();

    public void agregarError(String error) {
        if (error != null && !error.trim().isEmpty()) {
            errores.add(error);
        }
    }

    public void generarReporte(String rutaArchivo) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo))) {

            bw.write("""
                <!DOCTYPE html>
                <html lang="es">
                <head>
                <meta charset="UTF-8">
                <meta name="viewport"
                      content="width=device-width, initial-scale=1.0">

                <title>Reporte de errores semánticos</title>

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

                .barra h2 {
                    margin: 0;
                    font-size: 20px;
                }

                .contador {
                    background: #fee2e2;
                    color: #b91c1c;
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

                th:first-child,
                td:first-child {
                    width: 80px;
                    text-align: center;
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

                .numero {
                    color: #64748b;
                    font-weight: 600;
                }

                .error {
                    color: #b91c1c;
                    font-weight: 500;
                    overflow-wrap: anywhere;
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
                    <h1>Reporte de errores semánticos</h1>
                    <p>
                        Errores detectados durante el análisis semántico
                    </p>
                </header>

                <main>
                    <div class="barra">
                        <h2>Listado de errores</h2>
                        <span class="contador">
                """);

            bw.write(errores.size() + (errores.size() == 1 ? " error" : " errores"));

            bw.write("""
                        </span>
                    </div>

                    <div class="tabla-contenedor">
                    <table>
                    <thead>
                    <tr>
                        <th>No.</th>
                        <th>Error detectado</th>
                    </tr>
                    </thead>
                    <tbody>
                """);

            if (errores.isEmpty()) {

                bw.write("""
                    <tr>
                        <td colspan="2" class="vacio">
                            No hay errores semánticos registrados.
                        </td>
                    </tr>
                    """);

            } else {

                int numero = 1;
                for (String error : errores) {
                    bw.write("<tr>");
                    bw.write("<td class='numero'>" + numero++ + "</td>");
                    bw.write("<td class='error'>"
                            + escaparHTML(error) + "</td>");
                    bw.write("</tr>\n");
                }
            }

            bw.write("""
                    </tbody>
                    </table>
                    </div>
                </main>

                <footer>
                    Reporte generado por el analizador semántico.
                </footer>

                </div>
                </body>
                </html>
                """);

        } catch (IOException e) {
            System.err.println("Error al generar el reporte semántico: " + e.getMessage());
        }
    }

    private String escaparHTML(String texto) {

        if (texto == null) {
            return "";
        }

        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }
}
