package ec.edu.monster.modelo;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Movimiento {

    private String cuenta;
    private int nromov;
    private Date fecha;
    private String tipo;
    private String accion;
    private double importe;

    public Movimiento() {
        // Constructor vacío
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public int getNromov() {
        return nromov;
    }

    public void setNromov(int nromov) {
        this.nromov = nromov;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public void setFecha(String fechaStr) {
        if (fechaStr == null || fechaStr.isEmpty()) {
            this.fecha = new Date();
            return;
        }
        String[] formats = new String[] {
            "MMM d, yyyy", "MMM dd, yyyy", "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "dd/MM/yyyy"
        };
        for (Locale loc : new Locale[] { Locale.getDefault(), Locale.forLanguageTag("es"), Locale.US }) {
            for (String fmt : formats) {
                try {
                    this.fecha = new SimpleDateFormat(fmt, loc).parse(fechaStr);
                    return;
                } catch (Exception ignored) {}
            }
        }
        this.fecha = new Date();
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }
}
