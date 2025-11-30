package com.example.prueba_2_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ListarActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private ArrayList<Estudiante> listaEstudiantes;
    private ArrayAdapter<Estudiante> adapter;
    private ListView listViewDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Inicializar lista y adaptador
        listaEstudiantes = new ArrayList<>();
        listViewDatos = findViewById(R.id.lista_estudiantes);

        // Adaptador personalizado
        adapter = new ArrayAdapter<Estudiante>(this, R.layout.item_estudiante, listaEstudiantes) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_estudiante, parent, false);
                }

                Estudiante estudiante = listaEstudiantes.get(position);

                TextView item_id = convertView.findViewById(R.id.item_id);
                TextView item_nombre = convertView.findViewById(R.id.item_nombre);
                TextView item_edad = convertView.findViewById(R.id.item_edad);

                // Ahora mostramos el ID real del estudiante (no posicional)
                item_id.setText("ID: " + estudiante.getId());
                item_nombre.setText(estudiante.getNombre() + " " + estudiante.getApellidos());
                item_edad.setText(estudiante.getEdad() + " años");

                return convertView;
            }
        };

        listViewDatos.setAdapter(adapter);

        // Botón para ir a AgregarActivity
        Button boton_agregar = findViewById(R.id.boton_agregar);
        boton_agregar.setOnClickListener(v -> {
            Intent intent = new Intent(ListarActivity.this, AgregarActivity.class);
            startActivity(intent);
        });

        // Listener para cada item del ListView
        listViewDatos.setOnItemClickListener((parent, view, position, id) -> {
            Estudiante estudianteSeleccionado = listaEstudiantes.get(position);
            Intent intent_detalle = new Intent(ListarActivity.this, VerActivity.class);
            // Pasamos el ID del documento de Firestore
            intent_detalle.putExtra("ESTUDIANTE_ID", estudianteSeleccionado.getDocumentId());
            startActivity(intent_detalle);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarEstudiantes(); // Cargar o refrescar los datos cada vez que la actividad es visible
    }

    private void cargarEstudiantes() {
        // Ordenar por el campo "id" en orden ascendente para mantener el orden correcto
        db.collection("estudiantes")
                .orderBy("id", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listaEstudiantes.clear(); // Limpiar la lista antes de volver a llenarla
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Estudiante estudiante = document.toObject(Estudiante.class);
                            estudiante.setDocumentId(document.getId()); // ¡MUY IMPORTANTE! Guardar el ID del documento
                            listaEstudiantes.add(estudiante);
                        }
                        adapter.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
                    } else {
                        Toast.makeText(ListarActivity.this, "Error al cargar estudiantes.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}