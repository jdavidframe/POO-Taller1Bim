/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jdavi
 */
public class RegistroAlumnos implements Serializable {

    private Alumno alumno;
    private List<Matricula> matriculas;
    private Historial historial;

    public RegistroAlumnos(Alumno alumno) {
        this();
        this.alumno = alumno;
    }

    public RegistroAlumnos() {
        matriculas = new ArrayList<>();
        historial = new Historial();
    }

    public void agregarMatricula(Matricula matricula) {
        matriculas.add(matricula);

        historial.insertarAlFinal(matricula);
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public List<Matricula> getMatriculas() {
        return matriculas;
    }

    public void setMatriculas(List<Matricula> matriculas) {
        this.matriculas = matriculas;
    }

    public Historial getHistorial() {
        return historial;
    }

    public void setHistorial(Historial historial) {
        this.historial = historial;
    }

    @Override
    public String toString() {
        return alumno.toString();
    }
}
