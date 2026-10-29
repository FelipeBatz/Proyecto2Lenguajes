/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend;

public class Simbolo {

    private String nombre;
    private String queEs;
    private String dondeExiste;
    private int linea;

    public Simbolo(String nombre, String queEs, String dondeExiste, int linea) {
        this.nombre = nombre;
        this.queEs = queEs;
        this.dondeExiste = dondeExiste;
        this.linea = linea;
    }

    public String getNombre() {
        return nombre;
    }

    public String getQueEs() {
        return queEs;
    }

    public String getDondeExiste() {
        return dondeExiste;
    }
    
    public int getLinea() {
        return linea;
    }

}
