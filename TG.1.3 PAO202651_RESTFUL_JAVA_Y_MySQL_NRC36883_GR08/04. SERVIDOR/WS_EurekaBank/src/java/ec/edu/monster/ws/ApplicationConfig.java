package ec.edu.monster.ws;

import java.util.Set;
import jakarta.ws.rs.core.Application;

/**
 * Configuración de la ruta base JAX-RS (/webresources).
 * @author Grupo 08 (Gustavo Molina / Daniela Tituaña)
 */
@jakarta.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(ec.edu.monster.ws.CoreBancarioResource.class);
    }
}

