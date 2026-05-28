/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.Alumno;
import modelo.Matricula;
import modelo.Pension;
import modelo.RegistroAlumnos;

/**
 *
 * @author jdavi
 */

public class Cartera {

    private RegistroAlumnos registro;

    public Cartera() {
    }

    public Cartera(RegistroAlumnos registro) {
        this.registro = registro;
    }

    public double calcularCarteraVencida(
            Alumno alumno) {

        if (registro == null
                || registro.getAlumno() == null) {
            return 0;
        }

        if (!registro.getAlumno()
                .getCedula()
                .equals(alumno.getCedula())) {
            return 0;
        }

        double total = 0;

        for (Matricula m
                : registro.getMatriculas()) {

            for (Pension p
                    : m.getPensiones()) {

                if ("VENCIDA"
                        .equalsIgnoreCase(
                                p.getEstado())) {

                    total += p.getValorFinal();
                }
            }
        }

        return total;
    }

    public boolean emitirAlertaMorosidad(
            Alumno alumno) {

        if (registro == null
                || registro.getAlumno() == null) {
            return false;
        }

        if (!registro.getAlumno()
                .getCedula()
                .equals(alumno.getCedula())) {
            return false;
        }

        int vencidas = 0;

        for (Matricula m
                : registro.getMatriculas()) {

            for (Pension p
                    : m.getPensiones()) {

                if ("VENCIDA"
                        .equalsIgnoreCase(
                                p.getEstado())) {

                    vencidas++;
                }
            }
        }

        return vencidas > 2;
    }

    public RegistroAlumnos getRegistro() {
        return registro;
    }

    public void setRegistro(
            RegistroAlumnos registro) {
        this.registro = registro;
    }
}
