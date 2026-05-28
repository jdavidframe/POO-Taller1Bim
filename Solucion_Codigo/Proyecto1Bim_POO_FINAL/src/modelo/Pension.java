/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.Serializable;
import java.util.Date;


/**
 *
 * @author jdavi
 */

public class Pension implements Serializable {

    private String mes;
    private double valorBase;
    private double valorDescuento;
    private double valorFinal;
    private Date fechaVencimiento;
    private String estado;

    public Pension() {
    }

    public Pension(String mes, double valorBase, Date fechaVencimiento) {
        this.mes = mes;
        this.valorBase = valorBase;
        this.fechaVencimiento = fechaVencimiento;
        this.estado = "PENDIENTE";
        this.valorDescuento = 0;
        this.valorFinal = valorBase;
        evaluarEstado();
    }

    public void aplicarDescuentos(java.util.List<Beca> becas) {
        double total = 0;

        for (Beca b : becas) {
            total += b.getPorcentajeDescuento();
        }

        if (total > 100) total = 100;

        valorDescuento = valorBase * (total / 100.0);
        valorFinal = valorBase - valorDescuento;
    }

    public void evaluarEstado() {
        Date hoy = new Date();
        if (fechaVencimiento.before(hoy)) {
            estado = "VENCIDA";
        } else {
            estado = "PENDIENTE";
        }
    }

    public void marcarPagada() {
        estado = "PAGADA";
    }

    public String getMes() {
        return mes;
    }

    public double getValorBase() {
        return valorBase;
    }

    public double getValorFinal() {
        return valorFinal;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public String getEstado() {
        return estado;
    }

    public double getValorDescuento() {
        return valorDescuento;
    }
}
