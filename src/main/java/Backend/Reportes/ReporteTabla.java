package Backend.Reportes;

import java.io.FileWriter;
import java.io.IOException;

public class ReporteTabla {

    private StringBuilder filas;
    private int contador;

    public ReporteTabla() {
        filas = new StringBuilder();
        contador = 0;
    }

    public void agregarDato(
            String nombre,
            String queEs,
            String dondeExiste,
            int linea) {

        contador++;

        filas.append("<tr>")
                .append("<td>").append(escaparHTML(nombre)).append("</td>")
                .append("<td>").append(escaparHTML(queEs)).append("</td>")
                .append("<td>").append(escaparHTML(dondeExiste)).append("</td>")
                .append("<td><span class='linea'>")
                .append(linea)
                .append("</span></td>")
                .append("</tr>\n");
    }

    public String generarHTML() {

        return """
        <!DOCTYPE html>
        <html lang="es">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport"
                  content="width=device-width, initial-scale=1.0">

            <title>Tabla de símbolos</title>

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
                    padding: 30px;
                    background: white;
                    border-radius: 16px;
                    box-shadow: 0 8px 30px
                                rgba(15, 23, 42, 0.08);
                }

                .encabezado {
                    margin-bottom: 25px;
                }

                h1 {
                    margin: 0 0 8px;
                    font-size: 27px;
                    color: #0f172a;
                }

                .descripcion {
                    color: #64748b;
                    font-size: 14px;
                }

                .tabla-contenedor {
                    overflow-x: auto;
                    border: 1px solid #e2e8f0;
                    border-radius: 12px;
                }

                table {
                    width: 100%;
                    border-collapse: collapse;
                    text-align: left;
                }

                thead {
                    background: #172554;
                    color: white;
                }

                th {
                    padding: 16px;
                    font-size: 13px;
                    font-weight: 600;
                    white-space: nowrap;
                }

                td {
                    padding: 15px 16px;
                    font-size: 14px;
                    border-bottom: 1px solid #e2e8f0;
                    vertical-align: top;
                }

                tbody tr:nth-child(even) {
                    background: #f8fafc;
                }

                tbody tr:hover {
                    background: #eff6ff;
                }

                tbody tr:last-child td {
                    border-bottom: none;
                }

                .linea {
                    display: inline-block;
                    padding: 5px 10px;
                    border-radius: 7px;
                    background: #dbeafe;
                    color: #1d4ed8;
                    font-weight: 600;
                }

                .pie {
                    margin-top: 18px;
                    color: #64748b;
                    font-size: 12px;
                }
            </style>
        </head>

        <body>
            <div class="contenedor">
                <div class="encabezado">
                    <h1>Reporte de símbolos</h1>
                    <div class="descripcion">
                        Información registrada durante el análisis
                    </div>
                </div>

                <div class="tabla-contenedor">
                    <table>
                        <thead>
                            <tr>
                                <th>Nombre</th>
                                <th>Qué es</th>
                                <th>Dónde existe</th>
                                <th>Línea</th>
                            </tr>
                        </thead>
                        <tbody>
        """ + filas.toString() + """
                        </tbody>
                    </table>
                </div>

                <div class="pie">
                    Total de registros: """ + contador + """
                </div>
            </div>
        </body>
        </html>
        """;
    }

    public void guardarHTML(String ruta) throws IOException {
        try (FileWriter escritor = new FileWriter(ruta)) {
            escritor.write(generarHTML());
        }
    }

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
