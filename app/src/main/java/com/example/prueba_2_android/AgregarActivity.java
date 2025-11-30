package com.example.prueba_2_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

public class AgregarActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtener instancia de Firestore
        db = FirebaseFirestore.getInstance();

        // Asociar Botones
        Button boton_agregar = findViewById(R.id.boton_guardar);
        Button boton_cancelar = findViewById(R.id.boton_cancelar);

        boton_agregar.setOnClickListener(v -> {
            // obtener valores de input
            EditText input_nombre = findViewById(R.id.input_nombre);
            EditText input_apellidos = findViewById(R.id.input_apellidos);
            EditText input_edad = findViewById(R.id.input_edad);

            String nombre = input_nombre.getText().toString();
            String apellido = input_apellidos.getText().toString();
            String edadStr = input_edad.getText().toString();

            // Validar campos
            if (nombre.isEmpty() || apellido.isEmpty() || edadStr.isEmpty()) {
                Toast.makeText(AgregarActivity.this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            int edad = Integer.parseInt(edadStr);

            // Crear objeto Estudiante
            Estudiante nuevoEstudiante = new Estudiante(nombre, apellido, edad);

            // Añadir a la colección "estudiantes" en Firestore
            db.collection("estudiantes")
                .add(nuevoEstudiante)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AgregarActivity.this, "Estudiante agregado exitosamente", Toast.LENGTH_SHORT).show();
                    // Redireccionar a Listar
                    Intent intent = new Intent(AgregarActivity.this, ListarActivity.class);
                    startActivity(intent);
                    finish(); // Finalizar para no volver con el botón atrás
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AgregarActivity.this, "Error al agregar estudiante: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
        });

        boton_cancelar.setOnClickListener(v -> {
            Intent intent = new Intent(AgregarActivity.this, ListarActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
