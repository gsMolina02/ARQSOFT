package ec.edu.monster.servicio;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ec.edu.monster.ws.RestApiClient;
import ec.edu.monster.ws.SoapClient;
import ec.edu.monster.wseurekaclient.MainActivity;
import ec.edu.monster.wseurekaclient.R;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsuario;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button btnConfigServer;
    private TextView txtServerActual;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar persistencia de configuración del servidor
        RestApiClient.init(this);

        editTextUsuario = findViewById(R.id.username);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.login_button);
        btnConfigServer = findViewById(R.id.btnConfigServer);
        txtServerActual = findViewById(R.id.txtServerActual);

        actualizarTextoServidor();

        btnConfigServer.setOnClickListener(v -> mostrarDialogoConfigServidor());

        buttonLogin.setOnClickListener(v -> {
            final String usuario = editTextUsuario.getText().toString().trim();
            final String password = editTextPassword.getText().toString().trim();

            if (usuario.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Por favor, ingrese usuario y contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            buttonLogin.setEnabled(false);
            buttonLogin.setText("Iniciando sesión...");

            executorService.execute(() -> {
                boolean valido = SoapClient.validarIngreso(usuario, password);
                runOnUiThread(() -> {
                    buttonLogin.setEnabled(true);
                    buttonLogin.setText("INICIAR SESIÓN");
                    if (valido) {
                        Toast.makeText(LoginActivity.this, "¡Bienvenido " + usuario + "!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("usuario", usuario);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_LONG).show();
                    }
                });
            });
        });
    }

    private void actualizarTextoServidor() {
        String host = RestApiClient.getActiveHost();
        txtServerActual.setText("Servidor: " + host);
    }

    private void mostrarDialogoConfigServidor() {
        final EditText input = new EditText(this);
        input.setText(RestApiClient.getActiveHost());
        input.setHint("Ej. 192.168.100.15:8080 o https://xxx.trycloudflare.com");
        input.setPadding(30, 20, 30, 20);

        new AlertDialog.Builder(this)
                .setTitle("Configurar Servidor")
                .setMessage("Ingrese la IP local del servidor (ej. 192.168.100.15:8080) o la URL del túnel Cloudflare para conexión inalámbrica:")
                .setView(input)
                .setPositiveButton("Probar y Guardar", (dialog, which) -> {
                    String nuevo = input.getText().toString().trim();
                    if (!nuevo.isEmpty()) {
                        verificarYGuardarServidor(nuevo);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void verificarYGuardarServidor(final String host) {
        Toast.makeText(this, "Probando conexión con " + host + "...", Toast.LENGTH_SHORT).show();
        executorService.execute(() -> {
            boolean ok = RestApiClient.testConnection(host);
            runOnUiThread(() -> {
                RestApiClient.saveServerHost(LoginActivity.this, host);
                actualizarTextoServidor();
                if (ok) {
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("¡Conexión Exitosa!")
                            .setMessage("El servidor respondió correctamente.\nServidor activo: " + host)
                            .setPositiveButton("Aceptar", null)
                            .show();
                } else {
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("Aviso de Conexión")
                            .setMessage("Se guardó el servidor: " + host + "\n(Nota: No se recibió respuesta en este instante. Verifique que el servidor esté encendido o que ambos estén en la misma red).")
                            .setPositiveButton("Entendido", null)
                            .show();
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
