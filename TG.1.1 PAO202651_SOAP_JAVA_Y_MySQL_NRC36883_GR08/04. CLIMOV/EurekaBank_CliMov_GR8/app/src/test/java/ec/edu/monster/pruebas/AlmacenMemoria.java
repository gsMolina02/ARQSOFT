package ec.edu.monster.pruebas;

import ec.edu.monster.util.ConfiguracionServidor;

/**
 * Reemplaza a las SharedPreferences del celular en las pruebas.
 */
class AlmacenMemoria implements ConfiguracionServidor.Almacen {

    String url;

    AlmacenMemoria() {
    }

    AlmacenMemoria(String url) {
        this.url = url;
    }

    @Override
    public String leer() {
        return url;
    }

    @Override
    public void guardar(String url) {
        this.url = url;
    }

    @Override
    public void borrar() {
        url = null;
    }
}
