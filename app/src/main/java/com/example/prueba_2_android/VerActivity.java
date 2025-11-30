package com.example.prueba_2_android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class VerActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String estudianteId;

    private TextView detalle_id, detalle_nombre, detalle_apellido, detalle_edad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Asociar vistas
        detalle_id = findViewById(R.id.detalle_id);
        detalle_nombre = findViewById(R.id.detalle_nombre);
        detalle_apellido = findViewById(R.id.detalle_apellido);
        detalle_edad = findViewById(R.id.detalle_edad);

        // Obtener el ID del documento desde el Intent
        estudianteId = getIntent().getStringExtra("ESTUDIANTE_ID");

        if (estudianteId != null && !estudianteId.isEmpty()) {
            cargarDatosEstudiante();
        } else {
            Toast.makeText(this, "Error: No se recibió el ID del estudiante", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Configurar botones
        Button boton_volver = findViewById(R.id.boton_volver);
        Button boton_editar = findViewById(R.id.boton_editar);
        Button boton_eliminar = findViewById(R.id.boton_eliminar);

        boton_volver.setOnClickListener(v -> finish());

        boton_editar.setOnClickListener(v -> {
            Intent intent_editar = new Intent(VerActivity.this, EditarActivity.class);
            intent_editar.putExtra("ESTUDIANTE_ID", estudianteId);
            startActivity(intent_editar);
        });

        boton_eliminar.setOnClickListener(v -> eliminarEstudiante());
    }

    private void cargarDatosEstudiante() {
        db.collection("estudiantes").document(estudianteId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Estudiante estudiante = documentSnapshot.toObject(Estudiante.class);
                        if (estudiante != null) {
                            // Mostrar el ID numérico en lugar del ID de Firestore
                            detalle_id.setText(String.valueOf(estudiante.getId()));
                            detalle_nombre.setText(estudiante.getNombre());
                            detalle_apellido.setText(estudiante.getApellidos());
                            detalle_edad.setText(estudiante.getEdad() + " años");
                        }
                    } else {
                        Toast.makeText(VerActivity.this, "El estudiante no existe.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(VerActivity.this, "Error al cargar los datos.", Toast.LENGTH_SHORT).show();
                });
    }

    private void eliminarEstudiante() {
        db.collection("estudiantes").document(estudianteId).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(VerActivity.this, "Estudiante eliminado exitosamente", Toast.LENGTH_SHORT).show();
                    // Redirigir a ListarActivity y limpiar el stack de actividades
                    Intent intent = new Intent(VerActivity.this, ListarActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(VerActivity.this, "Error al eliminar el estudiante: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}