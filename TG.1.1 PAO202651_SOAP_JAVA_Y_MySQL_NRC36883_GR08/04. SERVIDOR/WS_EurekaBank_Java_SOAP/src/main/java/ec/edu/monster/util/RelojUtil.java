package ec.edu.monster.util;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public final class RelojUtil {

    private RelojUtil() {
    }

    /**
     * Fecha y hora local sin fracciones de segundo: es lo que guardan las columnas datetime de MySQL,
     * así la hora devuelta al cliente coincide con la registrada.
     */
    public static LocalDateTime ahoraLocal(Clock reloj) {
        return LocalDateTime.now(reloj).truncatedTo(ChronoUnit.SECONDS);
    }
}
