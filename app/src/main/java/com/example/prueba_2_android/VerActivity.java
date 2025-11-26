package com.example.prueba_2_android;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class VerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // CODIGO PARA CONECTARNOS A NUESTRA BD
        SQLiteDatabase db = openOrCreateDatabase("BD_ESTUDIANTES", Context.MODE_PRIVATE, null);

        // 1.- CREAR TABLA
        db.execSQL("CREATE TABLE IF NOT EXISTS ESTUDIANTES (ID INTEGER PRIMARY KEY AUTOINCREMENT, NOMBRE VARCHAR, APELLIDOS VARCHAR, EDAD INTEGER)");

        // 2.- OBTENER DATOS DESDE INTENT
        int ID_ELEMENTO = getIntent().getIntExtra("ID", 0);

        // 3.- Hacer consulta SQL
        final Cursor cursor_detalle = db.rawQuery("select * from ESTUDIANTES WHERE ID=" + ID_ELEMENTO, null);

        // 4.- Obtener Objeto Estudiante a partir del cursor
        Estudiante objeto_estudiante = new Estudiante();

        // 5.- Obtener Indices para la consulta
        int INDICE_ID = cursor_detalle.getColumnIndex("ID");
        int INDICE_NOMBRE = cursor_detalle.getColumnIndex("NOMBRE");
        int INDICE_APELLIDO = cursor_detalle.getColumnIndex("APELLIDOS");
        int INDICE_EDAD = cursor_detalle.getColumnIndex("EDAD");

        // 6.- iterar consulta
        while (cursor_detalle.moveToNext()) {
            objeto_estudiante.ID = cursor_detalle.getInt(INDICE_ID);
            objeto_estudiante.NOMBRE = cursor_detalle.getString(INDICE_NOMBRE);
            objeto_estudiante.APELLIDOS = cursor_detalle.getString(INDICE_APELLIDO);
            objeto_estudiante.EDAD = cursor_detalle.getInt(INDICE_EDAD);
        }

        // 7.- Cargar informacion a UI
        TextView detalle_id = findViewById(R.id.detalle_id);
        TextView detalle_nombre = findViewById(R.id.detalle_nombre);
        TextView detalle_apellido = findViewById(R.id.detalle_apellido);
        TextView detalle_edad = findViewById(R.id.detalle_edad);

        Button boton_volver = findViewById(R.id.boton_volver);
        Button boton_editar = findViewById(R.id.boton_editar);
        Button boton_eliminar = findViewById(R.id.boton_eliminar);

        detalle_id.setText(String.valueOf(objeto_estudiante.ID));
        detalle_nombre.setText(objeto_estudiante.NOMBRE);
        detalle_apellido.setText(objeto_estudiante.APELLIDOS);
        detalle_edad.setText(objeto_estudiante.EDAD + " años");

        // Clicks de los botones
        boton_volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_listar = new Intent(VerActivity.this, ListarActivity.class);
                startActivity(intent_listar);
            }
        });

        boton_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_editar = new Intent(VerActivity.this, EditarActivity.class);
                intent_editar.putExtra("ID", ID_ELEMENTO);
                startActivity(intent_editar);
            }
        });

        boton_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear sql para eliminar
                String sql = "DELETE FROM ESTUDIANTES WHERE ID=?";

                // preparar consulta sql (statement)
                SQLiteStatement statement = db.compileStatement(sql);
                statement.bindString(1, String.valueOf(ID_ELEMENTO));
                statement.execute();

                Toast.makeText(VerActivity.this, "Estudiante eliminado", Toast.LENGTH_SHORT).show();

                // Redireccionar a Listar
                Intent intent = new Intent(VerActivity.this, ListarActivity.class);
                startActivity(intent);
            }
        });
    }
}