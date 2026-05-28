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

public class Alumno implements Serializable {

    private String cedula;
    private String nombre;
    private String nivelAcademico;
    private double promedio;

    public Alumno() {
    }

    public Alumno(String cedula,
            String nombre,
            String nivelAcademico,
            double promedio) {

        this.cedula = cedula;
        this.nombre = nombre;
        this.nivelAcademico = nivelAcademico;
        this.promedio = promedio;
    }

    public boolean esElegibleBecaMerito() {
        return promedio >= 9.0;
    }

    public Beca generarBecaMeritoSiAplica() {

        if (esElegibleBecaMerito()) {
            return new Beca(Beca.MERITO);
        }

        return null;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNivelAcademico() {
        return nivelAcademico;
    }

    public void setNivelAcademico(String nivelAcademico) {
        this.nivelAcademico = nivelAcademico;
    }

    public double getPromedio() {
        return promedio;
    }

}
