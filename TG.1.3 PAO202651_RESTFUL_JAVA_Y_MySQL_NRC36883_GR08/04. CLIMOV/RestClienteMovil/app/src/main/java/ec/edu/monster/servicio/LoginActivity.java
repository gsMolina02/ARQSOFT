package ec.edu.monster.servicio;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ec.edu.monster.ws.SoapClient;
import ec.edu.monster.wseurekaclient.MainActivity;
import ec.edu.monster.wseurekaclient.R;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsuario;
    private EditText editTextPassword;
    private Button buttonLogin;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsuario = findViewById(R.id.username);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.login_button);

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
                    buttonLogin.setText("Login");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
