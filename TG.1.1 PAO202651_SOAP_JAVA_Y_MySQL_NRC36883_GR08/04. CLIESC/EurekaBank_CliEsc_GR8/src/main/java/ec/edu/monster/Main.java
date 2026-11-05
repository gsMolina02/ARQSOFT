package ec.edu.monster;

import ec.edu.monster.controlador.ServidorControlador;
import ec.edu.monster.vista.FormLogin;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    private Main() {
    }

    public static void main(String[] args) {
        // Controles estándar (menús, barras de desplazamiento) con el aspecto de Windows; el texto usa el
        // suavizado ClearType del escritorio
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            // Se queda con el aspecto por defecto de Java
        }
        SwingUtilities.invokeLater(() -> new FormLogin(new ServidorControlador()).setVisible(true));
    }
}
