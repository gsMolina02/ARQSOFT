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

public class DepósitoActivity extends AppCompatActivity {

    private static final String TAG = "DepositoActivity";
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Spinner spinnerCuentas;
    private EditText editTextNumeroCuenta;
    private TextView txtInfoMoneda;
    private EditText editTextImporte;
    private Button buttonDepositar;
    private Button buttonBack;
    private final List<CuentaItem> listaCuentas = new ArrayList<>();
    private ArrayAdapter<CuentaItem> cuentasAdapter;
    private CuentaItem selectedItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposito);

        spinnerCuentas = findViewById(R.id.spinnerCuentas);
        editTextNumeroCuenta = findViewById(R.id.editTextNumeroCuenta);
        txtInfoMoneda = findViewById(R.id.txtInfoMoneda);
        editTextImporte = findViewById(R.id.editTextImporte);
        buttonDepositar = findViewById(R.id.buttonDepositar);
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

        buttonDepositar.setOnClickListener(v -> {
            String numeroCuenta = editTextNumeroCuenta.getText().toString().trim();
            String importeString = editTextImporte.getText().toString().trim();

            if (numeroCuenta.isEmpty() || importeString.isEmpty()) {
                Toast.makeText(DepósitoActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                BigDecimal importe = new BigDecimal(importeString);
                if (importe.compareTo(BigDecimal.ZERO) <= 0) {
                    Toast.makeText(DepósitoActivity.this, "El importe debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                    return;
                }
                registrarDeposito(numeroCuenta, importe);
            } catch (NumberFormatException e) {
                Toast.makeText(DepósitoActivity.this, "Importe numérico inválido", Toast.LENGTH_SHORT).show();
            }
        });

        cargarCuentasEnSegundoPlano();
    }

    private void actualizarDetalleMoneda(CuentaItem item) {
        if (item != null) {
            txtInfoMoneda.setText("📌 Moneda: " + item.getNombreMoneda() 
                    + " | Saldo: " + item.getSimboloMoneda() + " " + String.format(Locale.US, "%.2f", item.getSaldo()));
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

    private void registrarDeposito(final String numeroCuenta, final BigDecimal importe) {
        buttonDepositar.setEnabled(false);
        buttonDepositar.setText("Procesando...");

        executorService.execute(() -> {
            boolean resultado = SoapClient.regDeposito(numeroCuenta, importe.doubleValue());
            runOnUiThread(() -> {
                buttonDepositar.setEnabled(true);
                buttonDepositar.setText("REGISTRAR DEPÓSITO");
                if (resultado) {
                    mostrarDialogoExito(numeroCuenta, importe);
                } else {
                    mostrarDialogoError("No se pudo registrar el depósito. Verifique que la cuenta exista y esté activa.");
                }
            });
        });
    }

    private void mostrarDialogoExito(String cta, BigDecimal imp) {
        String sim = (selectedItem != null) ? selectedItem.getSimboloMoneda() : "$";
        new AlertDialog.Builder(DepósitoActivity.this)
                .setTitle("¡Depósito Exitoso!")
                .setMessage("Se depositaron " + sim + " " + imp + " correctamente en la cuenta: " + cta)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    editTextImporte.setText("");
                    cargarCuentasEnSegundoPlano();
                })
                .show();
    }

    private void mostrarDialogoError(String msg) {
        new AlertDialog.Builder(DepósitoActivity.this)
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
