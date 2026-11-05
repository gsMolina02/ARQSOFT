package ec.edu.monster.ws;

import ec.edu.monster.datos.FabricaConexionJndi;
import ec.edu.monster.datos.UnidadTrabajo;
import ec.edu.monster.util.DireccionesRed;
import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.List;
import java.util.logging.Logger;

/**
 * Origen de datos de la aplicación y mensajes al desplegar.
 * <p>
 * Payara crea el pool de conexiones a partir de @DataSourceDefinition al desplegar el WAR: no hace falta
 * crear recursos JDBC en la consola de administración ni se usa el pool MySQLPool de otros proyectos.
 * Se conecta con el usuario Monster (03_EurekaBank_Usuario_App.sql) usando mysql-connector-j de domain1/lib.
 */
@DataSourceDefinition(
        name = InicioAplicacion.ORIGEN_DATOS,
        className = "com.mysql.cj.jdbc.MysqlDataSource",
        serverName = "localhost",
        portNumber = 3306,
        databaseName = "eurekabank",
        user = "Monster",
        password = "Monster9",
        minPoolSize = 1,
        maxPoolSize = 20,
        properties = {
            "allowPublicKeyRetrieval=true",
            "useSSL=false",
            "connectTimeout=5000",
            "characterEncoding=UTF-8",
            // Descarta las conexiones que quedaron rotas si MySQL se reinició
            "fish.payara.is-connection-validation-required=true",
            "fish.payara.connection-validation-method=table",
            "fish.payara.validation-table-name=moneda"
        })
@WebListener
public class InicioAplicacion implements ServletContextListener {

    /** Nombre JNDI del origen de datos, visible solo dentro de esta aplicación. */
    public static final String ORIGEN_DATOS = "java:app/jdbc/EurekaBankGR8";

    private static final Logger REGISTRO = Logger.getLogger(InicioAplicacion.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent evento) {
        String contexto = evento.getServletContext().getContextPath();
        List<String> urls = DireccionesRed.crearUrlsServicio(DireccionesRed.obtenerIPv4Locales(), contexto);

        REGISTRO.info("Clientes de esta computadora: " + DireccionesRed.urlServicio("localhost", contexto));
        for (String url : urls) {
            REGISTRO.info("Clientes de otras computadoras de la red: " + url);
        }
        if (urls.isEmpty()) {
            REGISTRO.warning("No se encontró una IP de red: conecte la computadora al Wi-Fi o al cable de red.");
        }
        comprobarBaseDatos();
    }

    // Avisa al desplegar si MySQL no responde, para no descubrirlo recién cuando un cliente inicie sesión
    private static void comprobarBaseDatos() {
        try (UnidadTrabajo unidad = new FabricaConexionJndi(ORIGEN_DATOS).abrir()) {
            REGISTRO.info("Conexión con MySQL correcta (base de datos eurekabank).");
        } catch (Exception ex) {
            REGISTRO.severe("No se pudo conectar con MySQL: " + ex.getMessage() + " Verifique que el servicio "
                    + "MySQL80 esté iniciado y que se ejecutaron los scripts de 03. BDD (01, 02 y 03).");
        }
    }
}
