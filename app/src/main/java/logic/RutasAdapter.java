package logic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.transporteinteligenteapp.R;

import java.util.ArrayList;

import Modelo.Rutas;

public class RutasAdapter extends RecyclerView.Adapter<RutasAdapter.ViewHolder> {

    private ArrayList<Rutas> listaRutas;

    public RutasAdapter(ArrayList<Rutas> listaRutas) {
        this.listaRutas = listaRutas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ruta, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Rutas r = listaRutas.get(position);
        holder.txtNombre.setText(r.getNombreRuta());
        holder.txtDestino.setText("Destino: " + r.getDestino());
        holder.txtHora.setText("Hora llegada: " + r.getHoraLlegada());
        holder.txtDistancia.setText("Distancia: " + r.getDistancia() + " m");
        holder.txtTiempo.setText("Tiempo: " + r.getTiempo() + " seg");
    }

    @Override
    public int getItemCount() {
        return listaRutas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtDestino, txtHora, txtDistancia, txtTiempo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtDestino = itemView.findViewById(R.id.txtDestino);
            txtHora = itemView.findViewById(R.id.txtHora);
            txtDistancia = itemView.findViewById(R.id.txtDistancia);
            txtTiempo = itemView.findViewById(R.id.txtTiempo);
        }
    }
}
