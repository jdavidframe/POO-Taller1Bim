package modelo;

import java.io.Serializable;

/**
 * Representa una beca académica.
 * Solo existen dos tipos: MERITO (automática) y FAMILIAR (manual).
 */
public class Beca implements Serializable {

    public static final String FAMILIAR = "FAMILIAR";
    public static final String MERITO   = "MERITO";

    private String tipoBeca;
    private double porcentajeDescuento;

    public Beca() {
    }

    public Beca(String tipoBeca) {
        this.tipoBeca = tipoBeca;
        if (FAMILIAR.equals(tipoBeca)) {
            this.porcentajeDescuento = 15.0;
        } else if (MERITO.equals(tipoBeca)) {
            this.porcentajeDescuento = 50.0;
        } else {
            throw new IllegalArgumentException("Tipo de beca no permitido: " + tipoBeca);
        }
    }

    public double calcularDescuento(double valorBase) {
        return valorBase * (porcentajeDescuento / 100.0);
    }

    public String getTipoBeca() {
        return tipoBeca;
    }

    public double getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    @Override
    public String toString() {
        return tipoBeca + " (" + porcentajeDescuento + "%)";
    }
}
