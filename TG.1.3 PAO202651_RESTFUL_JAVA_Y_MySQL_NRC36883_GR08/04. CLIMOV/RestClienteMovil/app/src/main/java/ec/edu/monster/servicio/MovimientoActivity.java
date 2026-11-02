package ec.edu.monster.servicio;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ec.edu.monster.modelo.CuentaItem;
import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.modelo.MovimientoAdapter;
import ec.edu.monster.ws.SoapClient;
import ec.edu.monster.wseurekaclient.R;

public class MovimientoActivity extends AppCompatActivity {

    private static final String TAG = "MovimientoActivity";
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Spinner spinnerCuentas;
    private EditText editTextNumeroCuenta;
    private TextView txtInfoMoneda;
    private Button buttonConsultar, buttonBack;
    private RecyclerView recyclerViewMovimientos;
    private TextView textViewError;
    private MovimientoAdapter movimientoAdapter;

    private final List<CuentaItem> listaCuentas = new ArrayList<>();
    private ArrayAdapter<CuentaItem> cuentasAdapter;
    private CuentaItem selectedItem = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_movimiento);

        spinnerCuentas = findViewById(R.id.spinnerCuentas);
        editTextNumeroCuenta = findViewById(R.id.editTextNumeroCuenta);
        txtInfoMoneda = findViewById(R.id.txtInfoMoneda);
        buttonConsultar = findViewById(R.id.buttonConsultar);
        buttonBack = findViewById(R.id.buttonBack);
        recyclerViewMovimientos = findViewById(R.id.recyclerViewMovimientos);
        textViewError = findViewById(R.id.textViewError);

        // Configurar RecyclerView
        movimientoAdapter = new MovimientoAdapter(new ArrayList<>());
        recyclerViewMovimientos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMovimientos.setAdapter(movimientoAdapter);

        // Configurar Adaptador de Cuentas para el Spinner
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
                    consultarMovimientos(selectedItem.getCuenta());
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

        // Configurar botón Consultar
        buttonConsultar.setOnClickListener(v -> {
            String numeroCuenta = editTextNumeroCuenta.getText().toString().trim();
            if (!numeroCuenta.isEmpty()) {
                consultarMovimientos(numeroCuenta);
            } else {
                textViewError.setText("Por favor, ingrese o seleccione un número de cuenta.");
                textViewError.setVisibility(View.VISIBLE);
            }
        });

        // Configurar botón Volver
        buttonBack.setOnClickListener(v -> finish());

        // Cargar lista de cuentas
        cargarCuentas();
    }

    private void actualizarDetalleMoneda(CuentaItem item) {
        if (item != null) {
            txtInfoMoneda.setText("📌 Moneda: " + item.getNombreMoneda() 
                    + " | Saldo: " + item.getSimboloMoneda() + " " + String.format(Locale.US, "%.2f", item.getSaldo()));
            txtInfoMoneda.setTextColor(item.esDolares() ? Color.parseColor("#059669") : Color.parseColor("#1D4ED8"));
        }
    }

    private void cargarCuentas() {
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
                    consultarMovimientos(selectedItem.getCuenta());
                }
            });
        });
    }

    private void consultarMovimientos(String cuenta) {
        textViewError.setVisibility(View.GONE);
        buttonConsultar.setEnabled(false);

        executorService.execute(() -> {
            List<Movimiento> lista = SoapClient.traerMovimientos(cuenta);
            runOnUiThread(() -> {
                buttonConsultar.setEnabled(true);
                if (lista != null && !lista.isEmpty()) {
                    movimientoAdapter.updateMovimientos(lista);
                    textViewError.setVisibility(View.GONE);
                } else {
                    movimientoAdapter.updateMovimientos(new ArrayList<>());
                    textViewError.setText("No se encontraron movimientos registrados para la cuenta " + cuenta);
                    textViewError.setVisibility(View.VISIBLE);
                }
            });
        });
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
