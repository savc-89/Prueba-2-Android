package com.example.prueba_2_android;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EditarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //CODIGO PARA CONECTARNOS A NUESTRA BD
        SQLiteDatabase db = openOrCreateDatabase("BD_ESTUDIANTES", Context.MODE_PRIVATE,null);

        //1.- CREAR TABLA
        db.execSQL("CREATE TABLE IF NOT EXISTS ESTUDIANTES (ID INTEGER PRIMARY KEY AUTOINCREMENT,NOMBRE VARCHAR,APELLIDOS VARCHAR,EDAD INTEGER)");

        //2.- OBTENER DATOS DESDE INTENT
        int ID_ELEMENTO = getIntent().getIntExtra("ID", 0);

        //3 Hacer consulta SQL
        final Cursor cursor_detalle = db.rawQuery("select * from ESTUDIANTES WHERE ID="+ID_ELEMENTO,null);

        //4 Obtener Objeto Estudiante a partir del cursor
        Estudiante objeto_estudiante = new Estudiante();

        //5 Obtener Indices para la consulta
        int INDICE_ID = cursor_detalle.getColumnIndex("ID");
        int INDICE_NOMBRE = cursor_detalle.getColumnIndex("NOMBRE");
        int INDICE_APELLIDO = cursor_detalle.getColumnIndex("APELLIDOS");
        int INDICE_EDAD = cursor_detalle.getColumnIndex("EDAD");

        //6 iterar consulta
        if( cursor_detalle.moveToFirst() ) {
            //Poblamos nuestro objeto con la informacion de la base de datos
            //los metodos getInt y getString reciben un int que representa al indice obtenido en el paso 5
            objeto_estudiante.ID = cursor_detalle.getInt(INDICE_ID);
            objeto_estudiante.NOMBRE = cursor_detalle.getString(INDICE_NOMBRE);
            objeto_estudiante.APELLIDOS = cursor_detalle.getString(INDICE_APELLIDO);
            objeto_estudiante.EDAD = cursor_detalle.getInt(INDICE_EDAD);
        }

        //7 cargar informacion a UI
        //obtener elementos desde la UI
        TextView editar_titulo = findViewById(R.id.editar_titulo);
        EditText input_nombre = findViewById(R.id.input_nombre);
        EditText input_apellidos = findViewById(R.id.input_apellidos);
        EditText input_edad = findViewById(R.id.input_edad);

        //cargar info a inputs
        editar_titulo.setText("Editar Estudiante #"+objeto_estudiante.ID);
        input_nombre.setText(objeto_estudiante.NOMBRE);
        input_apellidos.setText(objeto_estudiante.APELLIDOS);
        input_edad.setText(String.valueOf(objeto_estudiante.EDAD)); //String.valueOf(objeto_estudiante.EDAD) -> devuelve EDAD (actualmente es int) como string

        //8 Asociar Botones
        Button boton_editar = findViewById(R.id.boton_editar);
        Button boton_eliminar = findViewById(R.id.boton_eliminar);
        Button boton_cancelar = findViewById(R.id.boton_cancelar);

        boton_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cuando se presione agregar

                //obtener valores de input
                String nombre = input_nombre.getText().toString();
                String apellido = input_apellidos.getText().toString();
                String edad = input_edad.getText().toString();

                //Crear insert
                String sql = "UPDATE ESTUDIANTES SET NOMBRE=?,APELLIDOS=?,EDAD=? WHERE ID=?";

                SQLiteStatement statement = db.compileStatement(sql);
                statement.bindString(1, nombre);
                statement.bindString(2,apellido);
                statement.bindString(3,edad);
                statement.bindString(4, String.valueOf(ID_ELEMENTO)); //String.valueOf(ID_ELEMENTO) -> devuelve ID_ELEMENTO (actualmente es int) como string
                statement.execute();

                //Redireccionar a Listar
                Intent intent = new Intent(EditarActivity.this , ListarActivity.class);
                startActivity(intent);
            }
        });

        boton_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sql = "DELETE FROM ESTUDIANTES WHERE ID = ?";
                SQLiteStatement statement = db.compileStatement(sql);
                statement.bindString(1, String.valueOf(ID_ELEMENTO));
                statement.execute();

                Intent intent = new Intent(EditarActivity.this, ListarActivity.class);
                startActivity(intent);
            }
        });

        boton_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditarActivity.this, ListarActivity.class);
                startActivity(intent);
            }
        });

    }
}
