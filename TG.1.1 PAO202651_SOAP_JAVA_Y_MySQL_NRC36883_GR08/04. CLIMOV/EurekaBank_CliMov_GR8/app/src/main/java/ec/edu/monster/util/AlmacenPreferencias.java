package ec.edu.monster.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Guarda la dirección del servidor en las preferencias del celular: se conserva al cerrar la aplicación.
 */
public class AlmacenPreferencias implements ConfiguracionServidor.Almacen {

    private static final String ARCHIVO = "eurekabank_gr08";
    private static final String CLAVE_URL = "servidor_soap";

    private final SharedPreferences preferencias;

    public AlmacenPreferencias(Context contexto) {
        preferencias = contexto.getApplicationContext().getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE);
    }

    @Override
    public String leer() {
        return preferencias.getString(CLAVE_URL, null);
    }

    @Override
    public void guardar(String url) {
        preferencias.edit().putString(CLAVE_URL, url).apply();
    }

    @Override
    public void borrar() {
        preferencias.edit().remove(CLAVE_URL).apply();
    }
}
