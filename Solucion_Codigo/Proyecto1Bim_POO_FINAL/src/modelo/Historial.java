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

public class Historial implements Serializable {

    private NodoHistorial cabeza;
    private NodoHistorial cola;
    private NodoHistorial actual;

    public Historial() {
    }

    public void insertarAlFinal(Matricula m) {

        NodoHistorial nuevo = new NodoHistorial(m);

        if (cabeza == null) {
            cabeza = nuevo;
            cola = nuevo;
            actual = cabeza;
            return;
        }

        cola.setSiguiente(nuevo);
        nuevo.setAnterior(cola);
        cola = nuevo;
    }

    public boolean tieneSiguiente() {
        return actual != null && actual.getSiguiente() != null;
    }

    public boolean tieneAnterior() {
        return actual != null && actual.getAnterior() != null;
    }

    public Matricula obtenerSiguiente() {

        if (tieneSiguiente()) {
            actual = actual.getSiguiente();
            return actual.getMatricula();
        }

        return null;
    }

    public Matricula obtenerAnterior() {

        if (tieneAnterior()) {
            actual = actual.getAnterior();
            return actual.getMatricula();
        }

        return null;
    }

    public Matricula obtenerActual() {

        if (actual == null) {
            return null;
        }

        return actual.getMatricula();
    }

    public void irInicio() {
        actual = cabeza;
    }

    public void irFinal() {
        actual = cola;
    }

    public NodoHistorial getCabeza() {
        return cabeza;
    }

    public void setCabeza(NodoHistorial cabeza) {
        this.cabeza = cabeza;
    }

    public NodoHistorial getCola() {
        return cola;
    }

    public void setCola(NodoHistorial cola) {
        this.cola = cola;
    }

    public NodoHistorial getActual() {
        return actual;
    }

    public void setActual(NodoHistorial actual) {
        this.actual = actual;
    }
}
