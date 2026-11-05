package ec.edu.monster.modelos.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Resultado de iniciarSesion (CU-01): el token que el cliente envía en cada operación posterior.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "respuestaSesion", propOrder = {"token", "usuario", "empleado", "minutosVigencia"})
public class RespuestaSesion extends Respuesta {

    private String token;
    private String usuario;
    private String empleado;
    private int minutosVigencia;

    public RespuestaSesion() {
    }

    public RespuestaSesion(int codigo, String mensaje, String token, String usuario, String empleado,
            int minutosVigencia) {
        super(codigo, mensaje);
        this.token = token;
        this.usuario = usuario;
        this.empleado = empleado;
        this.minutosVigencia = minutosVigencia;
    }

    public String getToken() {
        return token;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getEmpleado() {
        return empleado;
    }

    public int getMinutosVigencia() {
        return minutosVigencia;
    }
}
