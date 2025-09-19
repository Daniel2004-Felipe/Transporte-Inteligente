package com.example.transporteinteligenteapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import Modelo.Rutas;
import logic.DBConnection;
import logic.RutasAdapter;

public class RutasActiity extends AppCompatActivity {



        private RecyclerView recyclerView;
        private RutasAdapter adapter;
        private ArrayList<Rutas> listaRutas;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_rutas_actiity);

            recyclerView = findViewById(R.id.recyclerRutas);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            listaRutas = obtenerRutasDeDB();
            adapter = new RutasAdapter(listaRutas);
            recyclerView.setAdapter(adapter);
        }

        private ArrayList<Rutas> obtenerRutasDeDB() {
            ArrayList<Rutas> rutas = new ArrayList<>();
            DBConnection dbHelper = new DBConnection(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            Cursor cursor = db.rawQuery("SELECT * FROM rutas", null);
            if (cursor.moveToFirst()) {
                do {
                    Rutas r = new Rutas(
                            cursor.getInt(cursor.getColumnIndexOrThrow("usuario_id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("nombre_ruta")),
                            cursor.getString(cursor.getColumnIndexOrThrow("destino")),
                            cursor.getString(cursor.getColumnIndexOrThrow("hora_llegada")),
                            cursor.getDouble(cursor.getColumnIndexOrThrow("distancia")),
                            cursor.getDouble(cursor.getColumnIndexOrThrow("tiempo")),
                            cursor.getString(cursor.getColumnIndexOrThrow("paradero_origen")),
                            cursor.getString(cursor.getColumnIndexOrThrow("paradero_destino"))
                    );
                    rutas.add(r);
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();
            return rutas;
        }

    }
