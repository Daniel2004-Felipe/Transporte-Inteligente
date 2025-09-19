package com.example.transporteinteligenteapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

import Modelo.Usuario;
import logic.DBConnection;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private MaterialButton buttonLogin;
    private TextView textViewRegister;
    private DBConnection dbConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        initDatabase();
        setupClickListeners();
    }

    private void initViews() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegister = findViewById(R.id.textViewRegister);
    }

    private void initDatabase() {
        dbConnection = new DBConnection(this);
    }

    private void setupClickListeners() {
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realizarLogin();
            }
        });

        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void realizarLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Ingresa tu correo electrónico");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Ingresa tu contraseña");
            editTextPassword.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Formato de correo inválido");
            editTextEmail.requestFocus();
            return;
        }

        if (dbConnection.verificarCredenciales(email, password)) {
            Toast.makeText(this, "¡Bienvenido!", Toast.LENGTH_SHORT).show();

            // **AGREGAR ESTAS LÍNEAS AQUÍ:**
            Usuario usuario = dbConnection.obtenerUsuarioPorEmail(email);
            if (usuario != null) {
                SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
                prefs.edit().putInt("userId", usuario.getId()).apply();
                prefs.edit().putString("userEmail", email).apply();
            }

            Intent intent = new Intent(LoginActivity.this, MapaActivity.class);
            intent.putExtra("user_email", email);
            startActivity(intent);

            finish();

        } else {
            Toast.makeText(this, "Datos incorrectos. Verifica tu correo y contraseña.", Toast.LENGTH_LONG).show();

            editTextPassword.setText("");
            editTextEmail.requestFocus();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbConnection != null) {
            dbConnection.close();
        }
    }
}