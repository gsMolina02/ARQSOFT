package ec.edu.monster.servicio;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ec.edu.monster.modelo.CuentaItem;
import ec.edu.monster.ws.SoapClient;
import ec.edu.monster.wseurekaclient.R;

public class RetiroActivity extends AppCompatActivity {

    private static final String TAG = "RetiroActivity";
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Spinner spinnerCuentas;
    private EditText editTextNumeroCuenta;
    private TextView txtInfoMoneda;
    private EditText editTextImporte;
    private Button buttonRetirar;
    private Button buttonBack;
    private final List<CuentaItem> listaCuentas = new ArrayList<>();
    private ArrayAdapter<CuentaItem> cuentasAdapter;
    private CuentaItem selectedItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retiro);

        spinnerCuentas = findViewById(R.id.spinnerCuentas);
        editTextNumeroCuenta = findViewById(R.id.editTextNumeroCuenta);
        txtInfoMoneda = findViewById(R.id.txtInfoMoneda);
        editTextImporte = findViewById(R.id.editTextImporte);
        buttonRetirar = findViewById(R.id.buttonRetirar);
        buttonBack = findViewById(R.id.buttonBack);

        cuentasAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaCuentas);
        cuentasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCuentas.setAdapter(cuentasAdapter);

        spinnerCuentas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < listaCuentas.size()) {
                    selectedItem = listaCuentas.get(position);
                    editTextNumeroCuenta.setText(selectedItem.getCuenta());
                    actualizarDetalleMoneda(selectedItem);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        editTextNumeroCuenta.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String cta = s.toString().trim();
                for (CuentaItem c : listaCuentas) {
                    if (c.getCuenta().equalsIgnoreCase(cta)) {
                        selectedItem = c;
                        actualizarDetalleMoneda(c);
                        return;
                    }
                }
            }
        });

        buttonBack.setOnClickListener(v -> finish());

        buttonRetirar.setOnClickListener(v -> {
            String numeroCuenta = editTextNumeroCuenta.getText().toString().trim();
            String importeString = editTextImporte.getText().toString().trim();

            if (numeroCuenta.isEmpty() || importeString.isEmpty()) {
                Toast.makeText(RetiroActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                BigDecimal importe = new BigDecimal(importeString);
                if (importe.compareTo(BigDecimal.ZERO) <= 0) {
                    Toast.makeText(RetiroActivity.this, "El importe debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                    return;
                }
                registrarRetiro(numeroCuenta, importe);
            } catch (NumberFormatException e) {
                Toast.makeText(RetiroActivity.this, "Importe numérico inválido", Toast.LENGTH_SHORT).show();
            }
        });

        cargarCuentasEnSegundoPlano();
    }

    private void actualizarDetalleMoneda(CuentaItem item) {
        if (item != null) {
            txtInfoMoneda.setText("📌 Moneda: " + item.getNombreMoneda() 
                    + " | Saldo disponible: " + item.getSimboloMoneda() + " " + String.format(Locale.US, "%.2f", item.getSaldo()));
            txtInfoMoneda.setTextColor(item.esDolares() ? Color.parseColor("#059669") : Color.parseColor("#1D4ED8"));
        }
    }

    private void cargarCuentasEnSegundoPlano() {
        executorService.execute(() -> {
            List<CuentaItem> cuentas = SoapClient.listarCuentas();
            runOnUiThread(() -> {
                listaCuentas.clear();
                listaCuentas.addAll(cuentas);
                cuentasAdapter.notifyDataSetChanged();
                if (!listaCuentas.isEmpty()) {
                    selectedItem = listaCuentas.get(0);
                    editTextNumeroCuenta.setText(selectedItem.getCuenta());
                    actualizarDetalleMoneda(selectedItem);
                }
            });
        });
    }

    private void registrarRetiro(final String numeroCuenta, final BigDecimal importe) {
        buttonRetirar.setEnabled(false);
        buttonRetirar.setText("Procesando...");

        executorService.execute(() -> {
            boolean resultado = SoapClient.regRetiro(numeroCuenta, importe.doubleValue());
            runOnUiThread(() -> {
                buttonRetirar.setEnabled(true);
                buttonRetirar.setText("REGISTRAR RETIRO");
                if (resultado) {
                    mostrarDialogoExito(numeroCuenta, importe);
                } else {
                    mostrarDialogoError("No se pudo registrar el retiro. Verifique que la cuenta disponga de saldo suficiente.");
                }
            });
        });
    }

    private void mostrarDialogoExito(String cta, BigDecimal imp) {
        String sim = (selectedItem != null) ? selectedItem.getSimboloMoneda() : "$";
        new AlertDialog.Builder(RetiroActivity.this)
                .setTitle("¡Retiro Exitoso!")
                .setMessage("Se retiraron " + sim + " " + imp + " correctamente de la cuenta: " + cta)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    editTextImporte.setText("");
                    cargarCuentasEnSegundoPlano();
                })
                .show();
    }

    private void mostrarDialogoError(String msg) {
        new AlertDialog.Builder(RetiroActivity.this)
                .setTitle("Error en Operación")
                .setMessage(msg)
                .setPositiveButton("Entendido", null)
                .show();
    }

    public void onBackButtonClick(View v) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
