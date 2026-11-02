package ec.edu.monster.modelo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import ec.edu.monster.wseurekaclient.R;

public class MovimientoAdapter extends RecyclerView.Adapter<MovimientoAdapter.MovimientoViewHolder> {

    private List<Movimiento> movimientos;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public MovimientoAdapter(List<Movimiento> movimientos) {
        this.movimientos = movimientos;
    }

    @NonNull
    @Override
    public MovimientoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movimiento, parent, false);
        return new MovimientoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovimientoViewHolder holder, int position) {
        Movimiento movimiento = movimientos.get(position);
        holder.textViewCuenta.setText(movimiento.getCuenta());
        holder.textViewFecha.setText(dateFormat.format(movimiento.getFecha()));
        holder.textViewTipo.setText(movimiento.getTipo());
        holder.textViewAccion.setText(movimiento.getAccion());
        holder.textViewImporte.setText(String.valueOf(movimiento.getImporte()));
    }

    @Override
    public int getItemCount() {
        return movimientos.size();
    }

    public static class MovimientoViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewCuenta;
        public TextView textViewNroMovimiento;
        public TextView textViewFecha;
        public TextView textViewTipo;
        public TextView textViewAccion;
        public TextView textViewImporte;
        public TextView textViewReferencia;

        public MovimientoViewHolder(View itemView) {
            super(itemView);
            textViewCuenta = itemView.findViewById(R.id.textViewCuentaItem);
            textViewFecha = itemView.findViewById(R.id.textViewFechaItem);
            textViewTipo = itemView.findViewById(R.id.textViewTipoItem);
            textViewAccion = itemView.findViewById(R.id.textViewAccionItem);
            textViewImporte = itemView.findViewById(R.id.textViewImporteItem);
        }
    }
    public void updateMovimientos(List<Movimiento> nuevosMovimientos) {
        this.movimientos.clear();
        this.movimientos.addAll(nuevosMovimientos);
        notifyDataSetChanged();
    }

}


