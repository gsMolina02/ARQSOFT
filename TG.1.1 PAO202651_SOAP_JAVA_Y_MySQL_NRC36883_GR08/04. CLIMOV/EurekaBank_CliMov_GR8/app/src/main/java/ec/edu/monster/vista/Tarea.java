package ec.edu.monster.vista;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Ejecuta las llamadas al servicio SOAP fuera del hilo principal (Android no permite usar la red en él)
 * y entrega el resultado o el error de vuelta en el hilo principal, donde se actualiza la pantalla.
 * Equivale a la clase Tarea (SwingWorker) del cliente de escritorio.
 */
public final class Tarea {

    private final ExecutorService hilo = Executors.newSingleThreadExecutor();
    private final Handler principal = new Handler(Looper.getMainLooper());
    private volatile boolean cancelada;

    public <T> void ejecutar(Supplier<T> trabajo, Consumer<T> alTerminar, Consumer<RuntimeException> alFallar) {
        if (cancelada) {
            return;
        }
        hilo.execute(() -> {
            try {
                T resultado = trabajo.get();
                entregar(() -> alTerminar.accept(resultado));
            } catch (RuntimeException ex) {
                entregar(() -> alFallar.accept(ex));
            }
        });
    }

    /**
     * Se llama al cerrar la pantalla: las respuestas que lleguen después ya no la tocan.
     */
    public void cancelar() {
        cancelada = true;
        hilo.shutdownNow();
    }

    private void entregar(Runnable accion) {
        principal.post(() -> {
            if (!cancelada) {
                accion.run();
            }
        });
    }
}
