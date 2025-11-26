package com.example.prueba_2_android;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
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

public class AgregarActivity extends AppCompatActivity {

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

        // CODIGO PARA CONECTARNOS A NUESTRA BD
        SQLiteDatabase db = openOrCreateDatabase("BD_ESTUDIANTES", Context.MODE_PRIVATE, null);

        // 1.- CREAR TABLA
        db.execSQL("CREATE TABLE IF NOT EXISTS ESTUDIANTES (ID INTEGER PRIMARY KEY AUTOINCREMENT, NOMBRE VARCHAR, APELLIDOS VARCHAR, EDAD INTEGER)");

        // 2.- Asociar Botones
        Button boton_agregar = findViewById(R.id.boton_guardar);
        Button boton_cancelar = findViewById(R.id.boton_cancelar);

        boton_agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // obtener valores de input
                EditText input_nombre = findViewById(R.id.input_nombre);
                EditText input_apellidos = findViewById(R.id.input_apellidos);
                EditText input_edad = findViewById(R.id.input_edad);

                String nombre = input_nombre.getText().toString();
                String apellido = input_apellidos.getText().toString();
                String edad = input_edad.getText().toString();

                // Validar campos
                if (nombre.isEmpty() || apellido.isEmpty() || edad.isEmpty()) {
                    Toast.makeText(AgregarActivity.this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Crear insert
                String sql = "insert into ESTUDIANTES (NOMBRE,APELLIDOS,EDAD) values(?,?,?)";

                SQLiteStatement statement = db.compileStatement(sql);
                statement.bindString(1, nombre);
                statement.bindString(2, apellido);
                statement.bindString(3, edad);
                statement.execute();

                Toast.makeText(AgregarActivity.this, "Estudiante agregado exitosamente", Toast.LENGTH_SHORT).show();

                // Redireccionar a Listar
                Intent intent = new Intent(AgregarActivity.this, ListarActivity.class);
                startActivity(intent);
            }
        });

        boton_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AgregarActivity.this, ListarActivity.class);
                startActivity(intent);
            }
        });
    }
}