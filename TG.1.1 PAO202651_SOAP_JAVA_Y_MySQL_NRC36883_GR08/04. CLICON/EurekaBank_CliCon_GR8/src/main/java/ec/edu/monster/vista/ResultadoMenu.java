package ec.edu.monster.vista;

/**
 * Cómo terminó el menú principal.
 */
public enum ResultadoMenu {
    /** Cerró sesión o la sesión venció: vuelve al inicio de sesión. */
    CERRAR_SESION,
    /** Se perdió la conexión y el usuario quiere cambiar la dirección del servidor. */
    CONFIGURAR_SERVIDOR,
    /** Salir del programa. */
    SALIR
}
