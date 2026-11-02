package ec.edu.monster.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase de conexión JDBC a la base de datos MySQL eurekabank.
 * @author Grupo 08 (Gustavo Molina / Daniela Tituaña)
 */
public class AccesoDB {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/eurekabank?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "root";

    public AccesoDB() {
    }

    public static Connection getConnection() throws SQLException {
        Connection cn = null;
        try {
            String driver = "com.mysql.cj.jdbc.Driver";
            Class.forName(driver).getDeclaredConstructor().newInstance();
            cn = DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            throw e;
        } catch (ClassNotFoundException e) {
            throw new SQLException("ERROR, no se encuentra el driver MySQL Connector/J");
        } catch (Exception e) {
            throw new SQLException("ERROR, no se tiene acceso al servidor MySQL eurekabank");
        }
        return cn;
    }
}

