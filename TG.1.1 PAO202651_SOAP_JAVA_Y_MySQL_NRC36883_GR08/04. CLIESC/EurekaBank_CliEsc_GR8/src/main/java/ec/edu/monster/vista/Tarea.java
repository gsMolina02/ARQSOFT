package ec.edu.monster.vista;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.swing.SwingWorker;

/**
 * Ejecuta una llamada al servicio en segundo plano para que la ventana no se congele mientras espera
 * la respuesta de la red, y entrega el resultado (o el error) en el hilo de la interfaz.
 */
final class Tarea {

    private Tarea() {
    }

    static <T> void ejecutar(Supplier<T> trabajo, Consumer<T> alTerminar, Consumer<RuntimeException> alFallar) {
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() {
                return trabajo.get();
            }

            @Override
            protected void done() {
                try {
                    alTerminar.accept(get());
                } catch (ExecutionException ex) {
                    Throwable causa = ex.getCause();
                    alFallar.accept(causa instanceof RuntimeException propia
                            ? propia
                            : new IllegalStateException("Error inesperado: " + causa.getMessage(), causa));
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }.execute();
    }
}
