package ec.edu.monster.vista;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import ec.edu.monster.R;

/**
 * Borde de los campos: azul mientras se escribe (lo hace el drawable campo_texto) y rojo cuando el dato
 * no es válido. Al corregir el texto el borde vuelve a la normalidad.
 */
public final class ResaltadoCampo {

    private ResaltadoCampo() {
    }

    public static void aplicar(EditText... campos) {
        for (EditText campo : campos) {
            campo.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence texto, int inicio, int cantidad, int despues) {
                }

                @Override
                public void onTextChanged(CharSequence texto, int inicio, int antes, int cantidad) {
                }

                @Override
                public void afterTextChanged(Editable texto) {
                    quitarError(campo);
                }
            });
        }
    }

    public static void marcarError(EditText campo) {
        campo.setBackgroundResource(R.drawable.campo_texto_error);
    }

    public static void quitarError(EditText... campos) {
        for (EditText campo : campos) {
            campo.setBackgroundResource(R.drawable.campo_texto);
        }
    }
}
