package Backend.Analizadores;

import Backend.AST.NodoAST;
import Backend.ListaEnlazadaGenerica.ListaEnlazada;
import Backend.Simbolo;

public class AnalizadorSemantico {

    private ListaEnlazada<Simbolo> listadoSimbolos;
    private ListaEnlazada<NodoAST> listadoNodos;
    private ListaEnlazada<String> listaErrores;

    public AnalizadorSemantico(
            ListaEnlazada<Simbolo> listadoSimbolos,
            ListaEnlazada<NodoAST> listadoNodos) {

        this.listadoSimbolos = listadoSimbolos;
        this.listadoNodos = listadoNodos;
        this.listaErrores = new ListaEnlazada<>();
    }

    public void analizar() {

        listaErrores = new ListaEnlazada<>();
        validarAgentesDuplicados();
        validarEjecutar();
    }

    private void validarAgentesDuplicados() {
        try {
            for (int i = 0; i < listadoSimbolos.getTamañoLista(); i++) {
                Simbolo simbolo1 = listadoSimbolos.obtenerDatos(i);
                if (simbolo1.getQueEs().equals("AGENTE")) {
                    for (int j = i + 1; j < listadoSimbolos.getTamañoLista(); j++) {
                        Simbolo simbolo2 = listadoSimbolos.obtenerDatos(j);
                        if (simbolo2.getQueEs().equals("AGENTE")) {
                            if (simbolo1.getNombre().equals(simbolo2.getNombre())) {
                                String error = "Error semántico: el agente '"
                                        + simbolo2.getNombre()
                                        + "' está declarado más de una vez.";
                                listaErrores.agregarNuevo(error);
                                System.out.println(error);

                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error al validar agentes duplicados: "
                    + e.getMessage());
        }
    }

    private void validarEjecutar() {

        try {
            for (int i = 0; i < listadoNodos.getTamañoLista(); i++) {

                NodoAST nodo = listadoNodos.obtenerDatos(i);

                if (nodo.getTipo().equals("EJECUTAR")) {

                    boolean encontrado = false;

                    for (int j = 0; j < listadoSimbolos.getTamañoLista(); j++) {

                        Simbolo simbolo = listadoSimbolos.obtenerDatos(j);

                        if (simbolo.getQueEs().equals("AGENTE")
                                && simbolo.getNombre().equals(nodo.getLexema())) {

                            encontrado = true;
                            break;
                        }
                    }

                    if (!encontrado) {

                        String error = "Error semántico en línea "
                                + nodo.getFila()
                                + ": el agente '"
                                + nodo.getLexema()
                                + "' no existe.";

                        listaErrores.agregarNuevo(error);
                        System.out.println(error);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error al validar EJECUTAR: "
                    + e.getMessage());
        }
    }

    public ListaEnlazada<String> getListaErrores() {
        return listaErrores;
    }
}
