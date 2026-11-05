package ec.edu.monster.pruebas.falsos;

import ec.edu.monster.modelos.Cuenta;
import ec.edu.monster.modelos.SesionActiva;
import ec.edu.monster.modelos.Usuario;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public final class DatosPrueba {

    public static final LocalDateTime AHORA = LocalDateTime.of(2026, 11, 4, 10, 30, 0);
    public static final String TOKEN = "11111111-2222-3333-4444-555555555555";

    private DatosPrueba() {
    }

    /** Reloj fijo en AHORA (zona UTC, así la hora local es la indicada). */
    public static Clock reloj(LocalDateTime ahora) {
        return Clock.fixed(ahora.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
    }

    public static SesionActiva sesionMonster(LocalDateTime ultimaActividad) {
        return new SesionActiva(TOKEN, "Monster", "0001", "Monster EurekaBank", ultimaActividad);
    }

    public static SesionActiva sesionMonster() {
        return sesionMonster(AHORA);
    }

    public static Usuario usuarioMonster(String estado) {
        return new Usuario("Monster", "0001", "Monster EurekaBank", Usuario.calcularHash("Monster9"), estado);
    }

    public static Usuario usuarioMonster() {
        return usuarioMonster(Usuario.ESTADO_ACTIVO);
    }

    public static Cuenta cuenta(String numero, String saldo, String estado, String moneda, int contador) {
        Cuenta cuenta = new Cuenta();
        cuenta.setNumero(numero);
        cuenta.setSaldo(new BigDecimal(saldo));
        cuenta.setFechaApertura(LocalDate.of(2026, 1, 15));
        cuenta.setEstado(estado);
        cuenta.setContadorMovimientos(contador);
        cuenta.setCodigoCliente("00001");
        cuenta.setCedulaCliente("1712345675");
        cuenta.setNombreCliente("Carlos Andrés Pérez Salazar");
        cuenta.setCodigoMoneda(moneda);
        cuenta.setNombreMoneda("USD".equals(moneda) ? "Dólar estadounidense" : "Otra moneda");
        cuenta.setSimboloMoneda("$");
        cuenta.setCodigoSucursal("001");
        cuenta.setNombreSucursal("Matriz Quito");
        return cuenta;
    }

    public static Cuenta cuenta(String numero, String saldo, int contador) {
        return cuenta(numero, saldo, Cuenta.ESTADO_ACTIVO, "USD", contador);
    }
}
