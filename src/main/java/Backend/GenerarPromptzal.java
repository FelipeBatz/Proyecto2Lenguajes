/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend;

import Backend.ListaEnlazadaGenerica.ListaEnlazadaException;
import Backend.ListaEnlazadaGenerica.ListaEnlazada;
import Backend.AST.NodoAST;



public class GenerarPromptzal {

    private ListaEnlazada<NodoAST> listaNodos;
    private StringBuilder prompGenerado;
    private int limiteInicial;

    public GenerarPromptzal(ListaEnlazada<NodoAST> listaNodos) {
        this.listaNodos = listaNodos;
        prompGenerado = new StringBuilder();
        limiteInicial = 0;

    }

    public String generar() {
        prompGenerado.append("======================Prompt===========================\n");
        prompGenerado.append("  #Este prompt fue generado con Promptzal\n");
        for (int i = 0; i < listaNodos.getTamañoLista(); i++) {
            try {

                if (listaNodos.obtenerDatos(i).getLexema().equals("@modelo")) {
                    prompGenerado.append("  Modelo destino: " + listaNodos.obtenerDatos(i).getValor() + "\n");
                } else if (listaNodos.obtenerDatos(i).getLexema().equals("@rol")) {
                    prompGenerado.append("  Rol general: " + listaNodos.obtenerDatos(i).getValor() + "\n");
                } else if (listaNodos.obtenerDatos(i).getLexema().equals("@formato")) {
                    prompGenerado.append("  Formato de respuestas: " + listaNodos.obtenerDatos(i).getValor() + "\n");
                } else if (listaNodos.obtenerDatos(i).getLexema().equals("AGENTE")) {
                    prompGenerado.append("\n    ##Agente: " + listaNodos.obtenerDatos(i).getValor() + "\n");
                    generarPromptAgentes(i);
                }
                System.out.println(listaNodos.obtenerDatos(i).getValor());

            } catch (ListaEnlazadaException ex) {
                System.getLogger(GenerarPromptzal.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
            

        }
        generarPromptComandos();
        generarPromptResultado();
        
        System.out.println(prompGenerado.toString());
        return prompGenerado.toString();

    }

    public void generarPromptAgentes(int limiteFinal) {
        System.out.println("asdasd " + limiteFinal);
        int contador = 0;
        for (int i = limiteInicial; i < limiteFinal; i++) {
            contador++;
            try {

                if (listaNodos.obtenerDatos(i).getLexema().equals("CONTEXTO")) {
                    prompGenerado.append("  contexto: " + listaNodos.obtenerDatos(i).getValor() + "\n");
                    prompGenerado.append("  Variables disponibles:\n");
                } else if (listaNodos.obtenerDatos(i).getTipo().equals("VARIABLE")) {
                    prompGenerado.append("  -" + listaNodos.obtenerDatos(i).getLexema() + ": contenido del archivo: " + listaNodos.obtenerDatos(i).getValor() + "\n");
                }
            } catch (ListaEnlazadaException ex) {
                System.getLogger(GenerarPromptzal.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }
        limiteInicial = contador;
    }

    public void generarPromptComandos() {
        prompGenerado.append("\n  Intrucciones:\n");
        int contadorInstrucciones = 0;
        for (int i = 0; i < listaNodos.getTamañoLista(); i++) {
            try {
                if (listaNodos.obtenerDatos(i).getLexema().equals("PREGUNTAR")) {
                    contadorInstrucciones++;
                    prompGenerado.append("  " + contadorInstrucciones + ".Basandote en: " + listaNodos.obtenerDatos(i).getExpresion()
                            + " , Responde: " + listaNodos.obtenerDatos(i).getConectores()
                            + " . Guarda la respuesta como: " + listaNodos.obtenerDatos(i).getDestino()
                            + "\n");
                } else if (listaNodos.obtenerDatos(i).getLexema().equals("GENERAR")) {
                    contadorInstrucciones++;
                    prompGenerado.append("  " + contadorInstrucciones + ".Genera: " + listaNodos.obtenerDatos(i).getExpresion()
                            + " a partir de: " + listaNodos.obtenerDatos(i).getConectores()
                            + " . Guarda el resultado como: " + listaNodos.obtenerDatos(i).getDestino()
                            + "\n");
                } else if (listaNodos.obtenerDatos(i).getLexema().equals("RESUMIR")) {
                    contadorInstrucciones++;
                    prompGenerado.append("  " + contadorInstrucciones + ".Resume: " + listaNodos.obtenerDatos(i).getExpresion()
                            + " en un maximo de: " + listaNodos.obtenerDatos(i).getConectores()
                            + " , Guarda el resultado como: " + listaNodos.obtenerDatos(i).getDestino()
                            + "\n");
                } else if (listaNodos.obtenerDatos(i).getLexema().equals("ANALIZAR")) {
                    contadorInstrucciones++;
                    prompGenerado.append("  " + contadorInstrucciones + ".Analiza: " + listaNodos.obtenerDatos(i).getExpresion()
                            + "  Considerando: " + listaNodos.obtenerDatos(i).getConectores()
                            + " , Guarda el resultado como: " + listaNodos.obtenerDatos(i).getDestino()
                            + "\n");
                } else if (listaNodos.obtenerDatos(i).getLexema().equals("TRADUCIR")) {
                    contadorInstrucciones++;
                    prompGenerado.append("  " + contadorInstrucciones + ".Traduce: " + listaNodos.obtenerDatos(i).getExpresion()
                            + " al idioma: " + listaNodos.obtenerDatos(i).getConectores()
                            + ",    Guarda el resultado como: " + listaNodos.obtenerDatos(i).getDestino()
                            + "\n");
                } else if (listaNodos.obtenerDatos(i).getLexema().equals("CLASIFICAR")) {
                    contadorInstrucciones++;
                    prompGenerado.append("  " + contadorInstrucciones + ".Clasifica: " + listaNodos.obtenerDatos(i).getExpresion()
                            + "     en las categorías: " + listaNodos.obtenerDatos(i).getConectores()
                            + "     , Guarda el resultado como: " + listaNodos.obtenerDatos(i).getDestino()
                            + "\n");
                } else if (listaNodos.obtenerDatos(i).getLexema().equals("EXTRAER")) {
                    contadorInstrucciones++;
                    prompGenerado.append("  " + contadorInstrucciones + ".Extrae: " + listaNodos.obtenerDatos(i).getExpresion()
                            + "     a partir de : " + listaNodos.obtenerDatos(i).getConectores()
                            + "     , Guarda el resultado como: " + listaNodos.obtenerDatos(i).getDestino()
                            + "\n");
                }
            } catch (ListaEnlazadaException e) {
                System.out.println("Error al generar promprt");
            }

        }
    }
    
    private void generarPromptResultado() {
        prompGenerado.append("\n  ##Resultado Solicitado\n");
        for (int i = 0; i < listaNodos.getTamañoLista(); i++) {
            try {
                if (listaNodos.obtenerDatos(i).getTipo().equals("EXPORTAR")) {
                    prompGenerado.append("  -" + listaNodos.obtenerDatos(i).getValor() + "\n");
                }
            } catch (ListaEnlazadaException ex) {
                System.getLogger(GenerarPromptzal.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
            
        }
    }
}
