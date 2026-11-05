package ec.edu.monster.vista;

import android.graphics.Color;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Con targetSdk 36 la pantalla ocupa todo el celular, también detrás de la barra de estado y de la de
 * navegación. Aquí se activa ese modo en todas las versiones y se deja el espacio de las barras y del
 * teclado para que ningún control quede tapado.
 */
public final class AjusteBordes {

    private AjusteBordes() {
    }

    /**
     * @param superior vista que recibe el espacio de la barra de estado (null: la imagen llega hasta arriba)
     * @param inferior vista que recibe el espacio de la barra de navegación o del teclado
     */
    public static void aplicar(AppCompatActivity actividad, View raiz, View superior, View inferior) {
        // Íconos blancos sobre el azul (o la imagen) de arriba; oscuros sobre el fondo claro de abajo
        EdgeToEdge.enable(actividad, SystemBarStyle.dark(Color.TRANSPARENT),
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT));

        int superiorInicial = superior == null ? 0 : superior.getPaddingTop();
        int inferiorInicial = inferior.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(raiz, (vista, bordes) -> {
            Insets barras = bordes.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout());
            Insets teclado = bordes.getInsets(WindowInsetsCompat.Type.ime());

            vista.setPadding(barras.left, vista.getPaddingTop(), barras.right, vista.getPaddingBottom());
            if (superior != null) {
                superior.setPadding(superior.getPaddingLeft(), superiorInicial + barras.top,
                        superior.getPaddingRight(), superior.getPaddingBottom());
            }
            inferior.setPadding(inferior.getPaddingLeft(), inferior.getPaddingTop(), inferior.getPaddingRight(),
                    inferiorInicial + Math.max(barras.bottom, teclado.bottom));
            return WindowInsetsCompat.CONSUMED;
        });
    }
}
