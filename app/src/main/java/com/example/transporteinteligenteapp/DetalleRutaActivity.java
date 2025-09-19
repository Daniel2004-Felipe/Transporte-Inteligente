package com.example.transporteinteligenteapp;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import Modelo.Ruta;
import Modelo.Rutas;
import logic.DBConnection;

public class DetalleRutaActivity extends AppCompatActivity {


    private LinearLayout route1Container, route2Container, route3Container, bestRouteContainer;
    private Button selectButton;
    private TextView backArrow;

    private double distanciaRecibida;
    private double tiempoRecibido;
    private String origenRecibido;
    private String destinoRecibido;
    private String destinoBuscado;
    Button btnVerRutas;

    private int rutaSeleccionadaIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_ruta);

        inicializarVistas();
        recibirDatos();
        configurarRutas();
        configurarClickListeners();
    }

    private void inicializarVistas() {

        route1Container = findViewById(R.id.route1Container);
        route2Container = findViewById(R.id.route2Container);
        route3Container = findViewById(R.id.route3Container);
        bestRouteContainer = findViewById(R.id.bestRouteContainer);
        selectButton = findViewById(R.id.selectButton);
        backArrow = findViewById(R.id.backArrow);
        btnVerRutas = findViewById(R.id.btnVerRutas);

    }

    private void recibirDatos() {
        Intent intent = getIntent();
        distanciaRecibida = intent.getDoubleExtra("distancia", 0.0);
        tiempoRecibido = intent.getDoubleExtra("tiempo", 0.0);
        origenRecibido = intent.getStringExtra("origen");
        destinoRecibido = intent.getStringExtra("destino");

    }

    private void configurarRutas() {
        // Calcular horarios de llegada
        Calendar ahora = Calendar.getInstance();

        // Ruta 1 - La mejor
        ahora.add(Calendar.MINUTE, (int)(tiempoRecibido / 60));
        String hora1 = formatearHora(ahora);

        // Ruta 2 - 20 minutos más
        ahora.add(Calendar.MINUTE, 20);
        String hora2 = formatearHora(ahora);

        // Ruta 3 - 15 minutos más
        ahora.add(Calendar.MINUTE, 15);
        String hora3 = formatearHora(ahora);

        // Actualizar textos en las tarjetas
        actualizarTextoRuta(route1Container, "Ruta 1", hora1 + " hora de llegada");
        actualizarTextoRuta(route2Container, "Ruta 2", hora2 + " hora de llegada");
        actualizarTextoRuta(route3Container, "Ruta 3", hora3 + " hora de llegada");
    }

    private String formatearHora(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    private void actualizarTextoRuta(LinearLayout container, String titulo, String hora) {
        // Limpiar y agregar textos
        container.removeAllViews();

        TextView tituloTV = new TextView(this);
        tituloTV.setText(titulo);
        tituloTV.setTextSize(24);
        tituloTV.setTextColor(Color.parseColor("#333333"));
        tituloTV.setTypeface(null, Typeface.BOLD); // Bold
        container.addView(tituloTV);

        TextView horaTV = new TextView(this);
        horaTV.setText(hora);
        horaTV.setTextSize(14);
        horaTV.setTextColor(Color.parseColor("#888888"));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 8, 0, 0);
        horaTV.setLayoutParams(params);
        container.addView(horaTV);
    }

    private void configurarClickListeners() {
        backArrow.setOnClickListener(v -> finish());

        route1Container.setOnClickListener(v -> seleccionarRuta(0));
        route2Container.setOnClickListener(v -> seleccionarRuta(1));
        route3Container.setOnClickListener(v -> seleccionarRuta(2));
        bestRouteContainer.setOnClickListener(v -> seleccionarRuta(0)); // Mejor = Ruta 1

        selectButton.setOnClickListener(v -> guardarRutaSeleccionada());
        btnVerRutas.setOnClickListener(v -> {
            Intent intent = new Intent(DetalleRutaActivity.this, RutasActiity.class);
            startActivity(intent);
        });
    }

    private void seleccionarRuta(int index) {
        // Resetear colores
        route1Container.setBackgroundColor(Color.WHITE);
        route2Container.setBackgroundColor(Color.WHITE);
        route3Container.setBackgroundColor(Color.WHITE);
        bestRouteContainer.setBackgroundColor(Color.WHITE);

        // Marcar selección
        rutaSeleccionadaIndex = index;
        LinearLayout[] containers = {route1Container, route2Container, route3Container};
        containers[index].setBackgroundColor(Color.parseColor("#E1F5FE"));

        if (index == 0) {
            bestRouteContainer.setBackgroundColor(Color.parseColor("#E1F5FE"));
        }

        selectButton.setEnabled(true);
        selectButton.setBackgroundColor(Color.parseColor("#4A148C"));
    }

    private void guardarRutaSeleccionada() {
        if (rutaSeleccionadaIndex == -1) {
            Toast.makeText(this, "Selecciona una ruta", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener ID del usuario desde SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        int usuarioId = prefs.getInt("userId", 0);

        if (usuarioId == 0) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear ruta y guardar
        String[] nombres = {"Ruta 1", "Ruta 2", "Ruta 3"};
        double[] tiempos = {tiempoRecibido, tiempoRecibido * 1.3, tiempoRecibido * 1.6};
        double[] distancias = {distanciaRecibida, distanciaRecibida * 1.2, distanciaRecibida * 1.5};

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, (int)(tiempos[rutaSeleccionadaIndex] / 60));
        String horaLlegada = formatearHora(cal);

        Rutas rutaSeleccionada = new Rutas(
                usuarioId,
                nombres[rutaSeleccionadaIndex],
                destinoBuscado != null ? destinoBuscado : "Destino",
                horaLlegada,
                distancias[rutaSeleccionadaIndex],
                tiempos[rutaSeleccionadaIndex],
                origenRecibido != null ? origenRecibido : "Origen",
                destinoRecibido != null ? destinoRecibido : "Destino"
        );

        // Guardar en base de datos
        DBConnection db = new DBConnection(this);
        boolean guardado = guardarRutaEnDB(db, rutaSeleccionada);

        if (guardado) {
            Toast.makeText(this, "Ruta guardada correctamente", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al guardar ruta", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean guardarRutaEnDB(DBConnection db, Rutas ruta) {
        // Insertar en tabla rutas (asumiendo que existe)
        android.database.sqlite.SQLiteDatabase database = db.getWritableDatabase();
        android.content.ContentValues values = new android.content.ContentValues();

        values.put("usuario_id", ruta.getUsuarioId());
        values.put("nombre_ruta", ruta.getNombreRuta());
        values.put("destino", ruta.getDestino());
        values.put("hora_llegada", ruta.getHoraLlegada());
        values.put("distancia", ruta.getDistancia());
        values.put("tiempo", ruta.getTiempo());
        values.put("paradero_origen", ruta.getParaderoOrigen());
        values.put("paradero_destino", ruta.getParaderoDestino());

        try {
            long result = database.insert("rutas", null, values);
            database.close();
            return result != -1;
        } catch (Exception e) {
            database.close();
            return false;
        }
    }
}
