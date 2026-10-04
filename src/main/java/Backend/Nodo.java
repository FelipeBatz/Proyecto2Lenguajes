/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend;

import java.io.Serializable;

/**
 *
 * @author felip
 */
public class Nodo<T> {

    private T dato;
    private Nodo siguienteDato;

    public Nodo(T dato) {
        this.dato = dato;
    }

    public T getContenido() {
        return dato;
    }

    public void setContenido(T dato) {
        this.dato = dato;
    }

    public Nodo getSiguiente() {
        return siguienteDato;
    }

    public void setSiguiente(Nodo siguiente) {
        this.siguienteDato = siguiente;
    }
}

