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

public class TransferenciaActivity extends AppCompatActivity {

    private static final String TAG = "TransferenciaActivity";
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Spinner spinnerCuentaOrigen;
    private EditText editTextCuentaOrigen;
    private TextView txtMonedaOrigen;

    private Spinner spinnerCuentaDestino;
    private EditText editTextCuentaDestino;
    private TextView txtMonedaDestino;

    private TextView txtAlertaCompatibilidad;
    private EditText editTextImporte;
    private Button buttonTransferir;
    private Button buttonBack;

    private final List<CuentaItem> listaCuentasOrigen = new ArrayList<>();
    private final List<CuentaItem> listaCuentasDestino = new ArrayList<>();
    private ArrayAdapter<CuentaItem> adapterOrigen;
    private ArrayAdapter<CuentaItem> adapterDestino;

    private CuentaItem selectedOrigen = null;
    private CuentaItem selectedDestino = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transferencia);

        spinnerCuentaOrigen = findViewById(R.id.spinnerCuentaOrigen);
        editTextCuentaOrigen = findViewById(R.id.editTextCuentaOrigen);
        txtMonedaOrigen = findViewById(R.id.txtMonedaOrigen);

        spinnerCuentaDestino = findViewById(R.id.spinnerCuentaDestino);
        editTextCuentaDestino = findViewById(R.id.editTextCuentaDestino);
        txtMonedaDestino = findViewById(R.id.txtMonedaDestino);

        txtAlertaCompatibilidad = findViewById(R.id.txtAlertaCompatibilidad);
        editTextImporte = findViewById(R.id.editTextImporte);
        buttonTransferir = findViewById(R.id.buttonTransferir);
        buttonBack = findViewById(R.id.buttonBack);

        // Adaptador Origen
        adapterOrigen = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaCuentasOrigen);
        adapterOrigen.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCuentaOrigen.setAdapter(adapterOrigen);

        // Adaptador Destino
        adapterDestino = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaCuentasDestino);
        adapterDestino.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCuentaDestino.setAdapter(adapterDestino);

        // Listener Selección Origen
        spinnerCuentaOrigen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < listaCuentasOrigen.size()) {
                    selectedOrigen = listaCuentasOrigen.get(position);
                    editTextCuentaOrigen.setText(selectedOrigen.getCuenta());
                    actualizarDetalleMonedaOrigen(selectedOrigen);
                    verificarCompatibilidadMonedas();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Listener Selección Destino
        spinnerCuentaDestino.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < listaCuentasDestino.size()) {
                    selectedDestino = listaCuentasDestino.get(position);
                    editTextCuentaDestino.setText(selectedDestino.getCuenta());
                    actualizarDetalleMonedaDestino(selectedDestino);
                    verificarCompatibilidadMonedas();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // TextWatcher para sincronizar cuando se escribe cuenta manual
        editTextCuentaOrigen.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String cta = s.toString().trim();
                CuentaItem encontrada = buscarCuentaPorNumero(cta);
                if (encontrada != null) {
                    selectedOrigen = encontrada;
                    actualizarDetalleMonedaOrigen(encontrada);
                }
                verificarCompatibilidadMonedas();
            }
        });

        editTextCuentaDestino.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String cta = s.toString().trim();
                CuentaItem encontrada = buscarCuentaPorNumero(cta);
                if (encontrada != null) {
                    selectedDestino = encontrada;
                    actualizarDetalleMonedaDestino(encontrada);
                }
                verificarCompatibilidadMonedas();
            }
        });

        // Botón Volver
        buttonBack.setOnClickListener(v -> finish());

        // Botón Transferir
        buttonTransferir.setOnClickListener(v -> {
            String cuentaOrigen = editTextCuentaOrigen.getText().toString().trim();
            String cuentaDestino = editTextCuentaDestino.getText().toString().trim();
            String importeString = editTextImporte.getText().toString().trim();

            if (cuentaOrigen.isEmpty() || cuentaDestino.isEmpty() || importeString.isEmpty()) {
                Toast.makeText(TransferenciaActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (cuentaOrigen.equalsIgnoreCase(cuentaDestino)) {
                Toast.makeText(TransferenciaActivity.this, "La cuenta origen y destino deben ser distintas", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar compatibilidad de monedas
            if (selectedOrigen != null && selectedDestino != null) {
                if (selectedOrigen.esDolares() != selectedDestino.esDolares()) {
                    mostrarDialogoError("No se puede transferir:\nLa Cuenta Origen está en " 
                            + selectedOrigen.getNombreMoneda() + " y la Cuenta Destino está en " 
                            + selectedDestino.getNombreMoneda() + ".\nAmbas deben ser de la misma moneda.");
                    return;
                }
            }

            try {
                BigDecimal importe = new BigDecimal(importeString);
                if (importe.compareTo(BigDecimal.ZERO) <= 0) {
                    Toast.makeText(TransferenciaActivity.this, "El importe debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                    return;
                }
                registrarTransferencia(cuentaOrigen, cuentaDestino, importe);
            } catch (NumberFormatException e) {
                Toast.makeText(TransferenciaActivity.this, "Importe numérico inválido", Toast.LENGTH_SHORT).show();
            }
        });

        // Cargar cuentas
        cargarCuentasEnSegundoPlano();
    }

    private CuentaItem buscarCuentaPorNumero(String cta) {
        for (CuentaItem item : listaCuentasOrigen) {
            if (item.getCuenta().equalsIgnoreCase(cta)) return item;
        }
        return null;
    }

    private void actualizarDetalleMonedaOrigen(CuentaItem item) {
        if (item != null) {
            txtMonedaOrigen.setText("Moneda Origen: " + item.getNombreMoneda() 
                    + " | Saldo: " + item.getSimboloMoneda() + " " + String.format(Locale.US, "%.2f", item.getSaldo()));
            txtMonedaOrigen.setTextColor(item.esDolares() ? Color.parseColor("#059669") : Color.parseColor("#1D4ED8"));
        }
    }

    private void actualizarDetalleMonedaDestino(CuentaItem item) {
        if (item != null) {
            txtMonedaDestino.setText("Moneda Destino: " + item.getNombreMoneda() 
                    + " | Saldo: " + item.getSimboloMoneda() + " " + String.format(Locale.US, "%.2f", item.getSaldo()));
            txtMonedaDestino.setTextColor(item.esDolares() ? Color.parseColor("#059669") : Color.parseColor("#1D4ED8"));
        }
    }

    private void verificarCompatibilidadMonedas() {
        if (selectedOrigen != null && selectedDestino != null) {
            boolean mismoTipo = (selectedOrigen.esDolares() == selectedDestino.esDolares());
            if (mismoTipo) {
                txtAlertaCompatibilidad.setBackgroundColor(Color.parseColor("#DCFCE7"));
                txtAlertaCompatibilidad.setTextColor(Color.parseColor("#15803D"));
                txtAlertaCompatibilidad.setText("✓ Monedas compatibles: Ambas cuentas son en " + selectedOrigen.getNombreMoneda());
            } else {
                txtAlertaCompatibilidad.setBackgroundColor(Color.parseColor("#FEE2E2"));
                txtAlertaCompatibilidad.setTextColor(Color.parseColor("#B91C1C"));
                txtAlertaCompatibilidad.setText("⚠️ MONEDAS DISTINTAS: Origen (" + selectedOrigen.getSimboloMoneda() 
                        + ") vs Destino (" + selectedDestino.getSimboloMoneda() + "). Solo se permiten transferencias en la misma moneda.");
            }
        }
    }

    private void cargarCuentasEnSegundoPlano() {
        executorService.execute(() -> {
            List<CuentaItem> cuentas = SoapClient.listarCuentas();
            runOnUiThread(() -> {
                listaCuentasOrigen.clear();
                listaCuentasOrigen.addAll(cuentas);
                adapterOrigen.notifyDataSetChanged();

                listaCuentasDestino.clear();
                listaCuentasDestino.addAll(cuentas);
                adapterDestino.notifyDataSetChanged();

                if (!cuentas.isEmpty()) {
                    selectedOrigen = cuentas.get(0);
                    editTextCuentaOrigen.setText(selectedOrigen.getCuenta());
                    actualizarDetalleMonedaOrigen(selectedOrigen);

                    // Buscar una cuenta destino compatible con la misma moneda si es posible
                    int destIndex = 1;
                    for (int i = 1; i < cuentas.size(); i++) {
                        if (cuentas.get(i).esDolares() == selectedOrigen.esDolares()) {
                            destIndex = i;
                            break;
                        }
                    }
                    if (destIndex < cuentas.size()) {
                        spinnerCuentaDestino.setSelection(destIndex);
                        selectedDestino = cuentas.get(destIndex);
                        editTextCuentaDestino.setText(selectedDestino.getCuenta());
                        actualizarDetalleMonedaDestino(selectedDestino);
                    }
                    verificarCompatibilidadMonedas();
                }
            });
        });
    }

    private void registrarTransferencia(final String ctaOrigen, final String ctaDestino, final BigDecimal importe) {
        buttonTransferir.setEnabled(false);
        buttonTransferir.setText("Procesando Transferencia...");

        executorService.execute(() -> {
            boolean resultado = SoapClient.regTransferencia(ctaOrigen, ctaDestino, importe.doubleValue());
            runOnUiThread(() -> {
                buttonTransferir.setEnabled(true);
                buttonTransferir.setText("REGISTRAR TRANSFERENCIA");
                if (resultado) {
                    mostrarDialogoExito(ctaOrigen, ctaDestino, importe);
                } else {
                    mostrarDialogoError("No se pudo realizar la transferencia. Verifique que la cuenta origen disponga de saldo suficiente y ambas cuentas pertenezcan a la misma moneda.");
                }
            });
        });
    }

    private void mostrarDialogoExito(String ctaOrigen, String ctaDestino, BigDecimal imp) {
        String monedaStr = (selectedOrigen != null) ? selectedOrigen.getSimboloMoneda() : "$";
        new AlertDialog.Builder(TransferenciaActivity.this)
                .setTitle("¡Transferencia Exitosa!")
                .setMessage("Se transfirieron " + monedaStr + " " + imp + "\nDesde: " + ctaOrigen + "\nHacia: " + ctaDestino)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    editTextImporte.setText("");
                    cargarCuentasEnSegundoPlano();
                })
                .show();
    }

    private void mostrarDialogoError(String msg) {
        new AlertDialog.Builder(TransferenciaActivity.this)
                .setTitle("Aviso de Transferencia")
                .setMessage(msg)
                .setPositiveButton("Entendido", null)
                .show();
    }

    // Soporte para android:onClick="onBackButtonClick"
    public void onBackButtonClick(View v) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
