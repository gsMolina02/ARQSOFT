package ec.edu.monster.modelo;

import ec.edu.monster.cliente.dto.CuentaDTO;
import ec.edu.monster.cliente.dto.MovimientoDTO;
import ec.edu.monster.cliente.dto.RespuestaMovimientos;
import ec.edu.monster.util.FormatoUtil;
import java.util.Comparator;
import java.util.List;

/**
 * Datos de la pantalla de operaciones: el formulario de la operación elegida, el comprobante y la cuenta
 * consultada (datos y movimientos).
 */
public class OperacionesModelo {

    private TipoOperacion operacion = TipoOperacion.MOVIMIENTOS;
    private String cuenta;
    private String cuentaDestino;
    private String importe;

    // Datos solo para mostrar en la vista
    private String error;
    private String campoConError;
    private boolean sinConexion;
    private String resultadoTitulo;
    private String resultadoValor;
    private RespuestaMovimientos consulta;
    // Cuentas de la lista desplegable de los campos de cuenta (vacía si el servidor no respondió)
    private List<CuentaDTO> cuentas = List.of();

    public TipoOperacion getOperacion() {
        return operacion;
    }

    public void setOperacion(TipoOperacion operacion) {
        this.operacion = operacion == null ? TipoOperacion.MOVIMIENTOS : operacion;
    }

    public List<TipoOperacion> getOperaciones() {
        return List.of(TipoOperacion.values());
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getCuentaDestino() {
        return cuentaDestino;
    }

    public void setCuentaDestino(String cuentaDestino) {
        this.cuentaDestino = cuentaDestino;
    }

    public String getImporte() {
        return importe;
    }

    public void setImporte(String importe) {
        this.importe = importe;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error == null || error.endsWith(".") ? error : error + ".";
    }

    /** Campo que se resalta en rojo: "cuenta", "destino" o "importe". */
    public String getCampoConError() {
        return campoConError;
    }

    public void setCampoConError(String campoConError) {
        this.campoConError = campoConError;
    }

    /** true si el servidor no respondió: se ofrece cerrar sesión y configurar el servidor. */
    public boolean isSinConexion() {
        return sinConexion;
    }

    public void setSinConexion(boolean sinConexion) {
        this.sinConexion = sinConexion;
    }

    public String getResultadoTitulo() {
        if (resultadoTitulo != null) {
            return resultadoTitulo;
        }
        return operacion.isLlevaImporte() ? "Comprobante" : "Saldo disponible";
    }

    public void setResultadoTitulo(String resultadoTitulo) {
        this.resultadoTitulo = resultadoTitulo;
    }

    public boolean isHayResultado() {
        return resultadoTitulo != null;
    }

    public String getResultadoValor() {
        return resultadoValor == null ? "—" : resultadoValor;
    }

    public void setResultadoValor(String resultadoValor) {
        this.resultadoValor = resultadoValor;
    }

    public List<CuentaDTO> getCuentas() {
        return cuentas;
    }

    public void setCuentas(List<CuentaDTO> cuentas) {
        this.cuentas = cuentas;
    }

    public void setConsulta(RespuestaMovimientos consulta) {
        this.consulta = consulta;
    }

    public boolean isHayConsulta() {
        return consulta != null && consulta.cuenta() != null;
    }

    // Datos de la cuenta consultada como propiedades simples para las expresiones ${...} de la página
    private CuentaDTO datos() {
        return consulta == null ? null : consulta.cuenta();
    }

    public String getNumeroCuenta() {
        return isHayConsulta() ? datos().numero() : null;
    }

    public String getCliente() {
        return isHayConsulta() ? datos().cliente() : null;
    }

    public String getCedula() {
        return isHayConsulta() ? datos().cedula() : null;
    }

    public String getSucursal() {
        return isHayConsulta() ? datos().sucursal() : null;
    }

    public String getMoneda() {
        return isHayConsulta() ? datos().moneda() : null;
    }

    public String getEstado() {
        return isHayConsulta() ? datos().estado() : null;
    }

    public String getMensajeConsulta() {
        return consulta == null ? null : consulta.mensaje();
    }

    public String getSaldo() {
        return isHayConsulta() ? FormatoUtil.formatearMonto(consulta.cuenta().saldo(), consulta.cuenta().simboloMoneda()) : null;
    }

    public boolean isCuentaActiva() {
        return isHayConsulta() && "ACTIVO".equals(consulta.cuenta().estado());
    }

    /** Movimientos de la cuenta consultada, el más reciente primero. */
    public List<MovimientoFila> getMovimientos() {
        if (!isHayConsulta()) {
            return List.of();
        }
        String simbolo = consulta.cuenta().simboloMoneda();
        return consulta.movimientos().stream()
                .sorted(Comparator.comparingInt(MovimientoDTO::numero).reversed())
                .map(movimiento -> new MovimientoFila(movimiento, simbolo))
                .toList();
    }
}
