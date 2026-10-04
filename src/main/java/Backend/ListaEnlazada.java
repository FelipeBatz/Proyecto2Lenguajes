/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend;

import Backend.ListaEnlazadaException;



/**
 *
 * @author felip
 */
public class ListaEnlazada<T> {

    private static final String MENSAJE_ERROR_INDICE = "Este indice sobrepasa el tamaño de la lista";
    private static final String MENSAJE_ERROR_LISTA_VACIA = "La lista esta vacia";
    private Nodo inicioLista;
    private Nodo finLista;
    private int tamañoLista;

    public ListaEnlazada() {
        tamañoLista = 0;
    }

    public void agregarNuevo(T dato) {
        Nodo<T> nuevo = new Nodo(dato);
        if (esVacia()) {
            inicioLista = nuevo;
        } else {
            finLista.setSiguiente(nuevo);
        }
        finLista = nuevo;
        tamañoLista++;
    }

    public boolean esVacia() {
        return inicioLista == null;
    }

    public T obtenerDatos(int index) throws ListaEnlazadaException {
        Nodo<T> nodoBuscado = obtenerNodo(index);
        return nodoBuscado.getContenido();
    }

    private Nodo obtenerNodo(int index) throws ListaEnlazadaException {
        if (index < 0 || index >= tamañoLista) {
            throw new ListaEnlazadaException(MENSAJE_ERROR_INDICE);
        }
        Nodo<T> actual = inicioLista;
        for (int i = 0; i < index; i++) {
            actual = actual.getSiguiente();
        }
        return actual;
    }

    public void eliminar(int index) throws ListaEnlazadaException {
        if (index < 0 || index >= tamañoLista) {
            throw new ListaEnlazadaException(MENSAJE_ERROR_INDICE);
        }

        if (index == 0) {
            inicioLista = inicioLista.getSiguiente();

            if (inicioLista == null) {
                finLista = null;
            }
        } else {

            Nodo<T> anterior = obtenerNodo(index - 1);
            Nodo<T> nodoAEliminar = anterior.getSiguiente();
            Nodo<T> siguiente = nodoAEliminar.getSiguiente();

            anterior.setSiguiente(siguiente);
        }
        tamañoLista--;
    }
    
    public void eliminarUltimo() throws ListaEnlazadaException {
        if (esVacia()) {
            throw new ListaEnlazadaException(MENSAJE_ERROR_LISTA_VACIA);
        }

        if (tamañoLista == 1) {
            inicioLista = null;
            finLista = null;
        } else {
            Nodo<T> penultimo = obtenerNodo(tamañoLista - 2);
            penultimo.setSiguiente(null);
            finLista = penultimo;
        }

        tamañoLista--;

    }

    public int getTamañoLista() {
        return tamañoLista;
    }
    
    
}