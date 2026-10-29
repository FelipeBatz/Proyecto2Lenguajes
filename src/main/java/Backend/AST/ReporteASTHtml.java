package Backend.AST;

import Backend.ListaEnlazadaGenerica.ListaEnlazada;
import Backend.ListaEnlazadaGenerica.ListaEnlazadaException;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ReporteASTHtml {

    private ListaEnlazada<NodoAST> nodos;

    public ReporteASTHtml() {
        nodos = new ListaEnlazada<>();
    }

    public void generar(NodoAST raiz, String ruta)
            throws IOException {

        if (raiz == null) {
            throw new IllegalArgumentException(
                    "No existe una raíz AST para mostrar."
            );
        }

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(ruta),
                        StandardCharsets.UTF_8))) {

            writer.write("""
                <!DOCTYPE html>
                <html lang="es">
                <head>
                <meta charset="UTF-8">
                <meta name="viewport"
                      content="width=device-width, initial-scale=1.0">

                <title>Árbol AST - PromptZal</title>

                <style>
                    * {
                        box-sizing: border-box;
                    }

                    body {
                        margin: 0;
                        min-height: 100vh;
                        font-family: Consolas, monospace;
                        background: #080f20;
                        color: #e5edff;
                    }

                    header {
                        padding: 24px 30px;
                        border-bottom: 1px solid #25395f;
                        background: #0c162b;
                    }

                    header h1 {
                        margin: 0 0 8px;
                        font-size: 23px;
                        letter-spacing: 2px;
                        color: #91baff;
                    }

                    header p {
                        color: #94a9cc;
                        margin: 0;
                        font-size: 13px;
                    }

                    .acciones {
                        padding: 15px 25px;
                        display: flex;
                        gap: 10px;
                    }

                    button {
                        border: 1px solid #34517f;
                        background: #152747;
                        color: #e5edff;
                        border-radius: 7px;
                        padding: 9px 14px;
                        cursor: pointer;
                        font-family: inherit;
                    }

                    button:hover {
                        background: #20385f;
                    }

                    .contenedor {
                        margin: 15px;
                        padding: 30px 20px;
                        min-height: 400px;
                        overflow: auto;
                        border: 1px solid #29416b;
                        border-radius: 18px;
                        background-color: #0b1428;
                        background-image: radial-gradient(
                            #243655 1px,
                            transparent 1px
                        );
                        background-size: 22px 22px;
                    }

                    .arbol {
                        width: max-content;
                        min-width: 100%;
                        display: flex;
                        justify-content: center;
                    }

                    .arbol ul {
                        display: flex;
                        position: relative;
                        justify-content: center;
                        padding: 28px 0 0;
                        margin: 0;
                    }

                    .arbol li {
                        position: relative;
                        list-style: none;
                        text-align: center;
                        padding: 22px 8px 0;
                    }

                    .arbol li::before,
                    .arbol li::after {
                        content: '';
                        position: absolute;
                        top: 0;
                        right: 50%;
                        width: 50%;
                        height: 22px;
                        border-top: 2px solid #304b78;
                    }

                    .arbol li::after {
                        right: auto;
                        left: 50%;
                        border-left: 2px solid #304b78;
                    }

                    .arbol li:only-child::before,
                    .arbol li:only-child::after {
                        display: none;
                    }

                    .arbol li:first-child::before,
                    .arbol li:last-child::after {
                        border: 0;
                    }

                    .arbol li:last-child::before {
                        border-right: 2px solid #304b78;
                        border-radius: 0 8px 0 0;
                    }

                    .arbol li:first-child::after {
                        border-radius: 8px 0 0 0;
                    }

                    .arbol ul ul::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: 50%;
                        height: 28px;
                        border-left: 2px solid #304b78;
                    }

                    .arbol > .raiz > li {
                        padding-top: 0;
                    }

                    .arbol > .raiz > li::before,
                    .arbol > .raiz > li::after,
                    .arbol > .raiz > li > .tarjeta::before {
                        display: none;
                    }

                    .tarjeta {
                        position: relative;
                        display: inline-flex;
                        flex-direction: column;
                        align-items: center;
                        justify-content: center;
                        gap: 7px;
                        min-width: 145px;
                        max-width: 260px;
                        min-height: 70px;
                        padding: 13px 18px;
                        border: 1px solid #2c4774;
                        border-radius: 13px;
                        background: #17294b;
                        color: #f1f5ff;
                        box-shadow: 0 5px 18px #0003;
                        cursor: pointer;
                    }

                    .tarjeta:hover {
                        border-color: #38d9f5;
                        transform: translateY(-2px);
                    }

                    .raiz-nodo {
                        background: #102b4c;
                        border-color: #27d9f5;
                        color: #38ddf5;
                    }

                    .linea {
                        font-size: 10px;
                        color: #7e9bc7;
                    }

                    .tipo {
                        font-size: 13px;
                                                color: #9bb9eb;
                    }

                    .oculto > ul {
                        display: none !important;
                    }

                    .oculto > .tarjeta {
                        border-color: #f5b642;
                        box-shadow: 0 0 15px #f5b64244;
                    }

                    .vacio {
                        color: #7e9bc7;
                        font-size: 12px;
                    }
                </style>
                </head>

                <body>
                    <header>
                        <h1>ÁRBOL SINTÁCTICO ABSTRACTO</h1>
                        <p>PromptZal · Representación gráfica del AST</p>
                    </header>

                    <div class="acciones">
                        <button onclick="expandirTodo()">
                            Expandir todo
                        </button>

                        <button onclick="contraerTodo()">
                            Contraer ramas
                        </button>
                    </div>

                    <main class="contenedor">
                        <div class="arbol">
                            <ul class="raiz">
                """);

            escribirNodo(writer, raiz, true);

            writer.write("""
                            </ul>
                        </div>
                    </main>

                    <script>
                        document.querySelectorAll('.tarjeta')
                            .forEach(function(tarjeta) {
                                tarjeta.addEventListener('click', function() {
                                    const padre = tarjeta.parentElement;

                                    if (padre.querySelector(':scope > ul')) {
                                        padre.classList.toggle('oculto');
                                    }
                                });
                            });

                        function expandirTodo() {
                            document.querySelectorAll('.arbol li')
                                .forEach(function(nodo) {
                                    nodo.classList.remove('oculto');
                                });
                        }

                        function contraerTodo() {
                            document.querySelectorAll('.arbol li')
                                .forEach(function(nodo) {
                                    if (nodo.querySelector(':scope > ul')) {
                                        nodo.classList.add('oculto');
                                    }
                                });

                            const raiz = document.querySelector('.arbol > .raiz');
                            if (raiz && raiz.firstElementChild) {
                                raiz.firstElementChild.classList.remove('oculto');
                            }
                        }
                    </script>
                </body>
                </html>
                """);
        }
    }

    private void escribirNodo(BufferedWriter writer, NodoAST nodo, boolean esRaiz) throws IOException {

        writer.write("<li>");
        writer.write("<div class='tarjeta");

        if (esRaiz) {
            writer.write(" raiz-nodo");
        }

        writer.write("'>");
        writer.write("<strong>");
        writer.write(nodo.getTipo() + " " + nodo.getLexema());
        writer.write("</strong>");
        writer.write("<span class='tipo'>");
        writer.write("valor: " + nodo.getValor());
        writer.write("</span>");

        if (nodo.getFila() > 0) {
            writer.write("<span class='linea'>Línea ");
            writer.write(String.valueOf(nodo.getFila()));
            writer.write("</span>");
        } else {
            writer.write("<span class='linea'>Línea no registrada</span>");
        }
        writer.write("</div>");

        if (nodo.tieneHijos()) {
            writer.write("<ul>");
            for (int i = 0; i < nodo.getHijos().getTamañoLista(); i++) {
                try {
                    NodoAST hijo = nodo.getHijos().obtenerDatos(i);
                    escribirNodo(writer, hijo, false);
                } catch (ListaEnlazadaException ex) {
                    throw new IOException("No se pudo recorrer el AST.", ex);
                }
            }
            writer.write("</ul>");
        }
        writer.write("</li>");

        nodos.agregarNuevo(nodo);
    }

    public ListaEnlazada<NodoAST> getNodos() {
        return nodos;
    }

}
