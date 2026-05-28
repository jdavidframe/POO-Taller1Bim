/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.Serializable;

/**
 *
 * @author jdavi
 */

public class NodoHistorial implements Serializable {

    private Matricula matricula;
    private NodoHistorial siguiente;
    private NodoHistorial anterior;

    public NodoHistorial() {
    }

    public NodoHistorial(Matricula matricula) {
        this.matricula = matricula;
    }

    public Matricula getMatricula() {
        return matricula;
    }

    public void setMatricula(Matricula matricula) {
        this.matricula = matricula;
    }

    public NodoHistorial getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoHistorial siguiente) {
        this.siguiente = siguiente;
    }

    public NodoHistorial getAnterior() {
        return anterior;
    }

    public void setAnterior(NodoHistorial anterior) {
        this.anterior = anterior;
    }
}
