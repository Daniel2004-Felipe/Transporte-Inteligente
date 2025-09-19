package com.example.transporteinteligenteapp;

<<<<<<< HEAD
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import logic.DBConnection;
import Modelo.Usuario;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPassword;
    private MaterialButton buttonRegister;
    private TextView textViewLogin;
    private DBConnection dbConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbConnection = new DBConnection(this);

        initViews();

        setupClickListeners();
    }

    private void initViews() {
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewLogin = findViewById(R.id.textViewLogin);
    }

    private void setupClickListeners() {
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
            }
        });
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ir a la actividad de login
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void registrarUsuario() {
        String nombre = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (!validarCampos(nombre, email, password)) {
            return;
        }

        if (dbConnection.emailExists(email)) {
            Toast.makeText(this, "Este correo electrónico ya está registrado", Toast.LENGTH_SHORT).show();
            return;
        }

        Usuario nuevoUsuario = new Usuario(nombre, email, password);

        if (dbConnection.registrarUsuario(nuevoUsuario)) {
            Toast.makeText(this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();

            limpiarCampos();

            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        } else {
            Toast.makeText(this, "Error al registrar usuario. Intenta nuevamente", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validarCampos(String nombre, String email, String password) {

        if (nombre.isEmpty()) {
            editTextName.setError("El nombre es requerido");
            editTextName.requestFocus();
            return false;
        }

        if (nombre.length() < 2) {
            editTextName.setError("El nombre debe tener al menos 2 caracteres");
            editTextName.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            editTextEmail.setError("El correo electrónico es requerido");
            editTextEmail.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Ingresa un correo electrónico válido");
            editTextEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("La contraseña es requerida");
            editTextPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            editTextPassword.setError("La contraseña debe tener al menos 6 caracteres");
            editTextPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void limpiarCampos() {
        editTextName.setText("");
        editTextEmail.setText("");
        editTextPassword.setText("");
=======
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
>>>>>>> c3628db39dc62b472976c8f5187b5eb85aa3341d
    }
}