package com.example.prueba_2_android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class EditarActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String estudianteId;

    private TextView editar_titulo;
    private EditText input_nombre, input_apellidos, input_edad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Asociar vistas
        editar_titulo = findViewById(R.id.editar_titulo);
        input_nombre = findViewById(R.id.input_nombre);
        input_apellidos = findViewById(R.id.input_apellidos);
        input_edad = findViewById(R.id.input_edad);

        // Obtener el ID del documento desde el Intent
        estudianteId = getIntent().getStringExtra("ESTUDIANTE_ID");

        if (estudianteId != null && !estudianteId.isEmpty()) {
            cargarDatosEstudiante();
        } else {
            Toast.makeText(this, "Error: No se recibió el ID del estudiante", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Configurar botones
        Button boton_editar = findViewById(R.id.boton_editar);
        Button boton_eliminar = findViewById(R.id.boton_eliminar);
        Button boton_cancelar = findViewById(R.id.boton_cancelar);

        boton_editar.setOnClickListener(v -> guardarCambios());
        boton_eliminar.setOnClickListener(v -> eliminarEstudiante());
        boton_cancelar.setOnClickListener(v -> finish());
    }

    private void cargarDatosEstudiante() {
        db.collection("estudiantes").document(estudianteId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Estudiante estudiante = documentSnapshot.toObject(Estudiante.class);
                        if (estudiante != null) {
                            editar_titulo.setText("Editar Estudiante");
                            input_nombre.setText(estudiante.getNombre());
                            input_apellidos.setText(estudiante.getApellidos());
                            input_edad.setText(String.valueOf(estudiante.getEdad()));
                        }
                    } else {
                        Toast.makeText(EditarActivity.this, "El estudiante no existe.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditarActivity.this, "Error al cargar los datos.", Toast.LENGTH_SHORT).show();
                });
    }

    private void guardarCambios() {
        String nombre = input_nombre.getText().toString();
        String apellidos = input_apellidos.getText().toString();
        String edadStr = input_edad.getText().toString();

        if (nombre.isEmpty() || apellidos.isEmpty() || edadStr.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int edad = Integer.parseInt(edadStr);

        // Primero obtenemos el estudiante actual para conservar su ID
        db.collection("estudiantes").document(estudianteId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Estudiante estudianteActual = documentSnapshot.toObject(Estudiante.class);

                        // Crear estudiante actualizado conservando el ID original
                        Estudiante estudianteActualizado = new Estudiante(nombre, apellidos, edad);
                        estudianteActualizado.setId(estudianteActual.getId()); // Mantener el ID original

                        // Actualizar en Firestore
                        db.collection("estudiantes").document(estudianteId).set(estudianteActualizado)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(EditarActivity.this, "Estudiante actualizado exitosamente", Toast.LENGTH_SHORT).show();
                                    finish(); // Volver a la actividad anterior (VerActivity)
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(EditarActivity.this, "Error al actualizar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditarActivity.this, "Error al obtener datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void eliminarEstudiante() {
        db.collection("estudiantes").document(estudianteId).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditarActivity.this, "Estudiante eliminado exitosamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditarActivity.this, ListarActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditarActivity.this, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}