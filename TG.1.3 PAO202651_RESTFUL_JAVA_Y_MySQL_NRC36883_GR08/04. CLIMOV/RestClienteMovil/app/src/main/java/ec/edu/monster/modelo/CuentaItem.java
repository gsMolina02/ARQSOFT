package ec.edu.monster.modelo;

public class CuentaItem {
    private String cuenta;
    private String titular;
    private double saldo;
    private String moneda;

    public CuentaItem() {
    }

    public CuentaItem(String cuenta, String titular, double saldo, String moneda) {
        this.cuenta = cuenta;
        this.titular = titular;
        this.saldo = saldo;
        this.moneda = moneda;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public boolean esDolares() {
        return moneda != null && (moneda.toLowerCase().contains("dolar") || moneda.toLowerCase().contains("usd"));
    }

    public String getSimboloMoneda() {
        return esDolares() ? "USD $" : "S/.";
    }

    public String getNombreMoneda() {
        return esDolares() ? "DÓLARES (USD $)" : "SOLES (S/.)";
    }

    public String getBadgeMoneda() {
        return esDolares() ? "[$ DÓLARES]" : "[S/. SOLES]";
    }

    @Override
    public String toString() {
        if (cuenta == null || cuenta.isEmpty()) {
            return titular != null ? titular : "-- Seleccione Cuenta --";
        }
        return getBadgeMoneda() + " " + cuenta + " - " + titular;
    }
}
