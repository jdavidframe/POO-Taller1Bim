/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author jdavi
 */

public class Matricula implements Serializable {

    private String periodoAcademico;
    private double costoBase;
    private Pension[] pensiones;
    private ArrayList<Beca> becas;

    public Matricula() {
        becas = new ArrayList<>();
    }

    public Matricula(String periodoAcademico, double costoBase) {
        this.periodoAcademico = periodoAcademico;
        this.costoBase = costoBase;
        this.becas = new ArrayList<>();
        inicializarPensiones();
    }

    private void inicializarPensiones() {

        pensiones = new Pension[10];

        String[] meses = {"Septiembre","Octubre","Noviembre","Diciembre",
            "Enero","Febrero","Marzo","Abril","Mayo","Junio"};

        double valorMensual = costoBase / 10.0;

        int anioInicio = Integer.parseInt(periodoAcademico.split("-")[0]);

        Calendar cal = Calendar.getInstance();

        for (int i = 0; i < pensiones.length; i++) {

            int mesCalendario = (Calendar.SEPTEMBER + i) % 12;
            int anio = (i < 4) ? anioInicio : anioInicio + 1;

            cal.set(Calendar.YEAR, anio);
            cal.set(Calendar.MONTH, mesCalendario);
            cal.set(Calendar.DAY_OF_MONTH, 25);

            Date fecha = cal.getTime();

            pensiones[i] = new Pension(meses[i], valorMensual, fecha);
        }
    }

    public boolean agregarBeca(Beca beca) {

        double total = 0;

        for (Beca b : becas) {
            total += b.getPorcentajeDescuento();
        }

        if (total + beca.getPorcentajeDescuento() > 100) {
            return false;
        }

        becas.add(beca);
        aplicarBecasAPensiones();

        return true;
    }

    public void aplicarBecasAPensiones() {
        for (Pension p : pensiones) {
            p.aplicarDescuentos(becas);
            p.evaluarEstado();
        }
    }

    public String getPeriodoAcademico() {
        return periodoAcademico;
    }

    public double getCostoBase() {
        return costoBase;
    }

    public Pension[] getPensiones() {
        return pensiones;
    }

    public ArrayList<Beca> getBecas() {
        return becas;
    }
}
