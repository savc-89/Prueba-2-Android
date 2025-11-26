package com.example.prueba_2_android;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ListarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // CODIGO PARA CONECTARNOS A NUESTRA BD
        SQLiteDatabase db = openOrCreateDatabase("BD_ESTUDIANTES", Context.MODE_PRIVATE, null);

        // 1.- CREAR TABLA
        db.execSQL("CREATE TABLE IF NOT EXISTS ESTUDIANTES (ID INTEGER PRIMARY KEY AUTOINCREMENT, NOMBRE VARCHAR, APELLIDOS VARCHAR, EDAD INTEGER)");

        // 2.- LEER LA TABLA Y CARGAR A LISTVIEW
        final Cursor cursor_listar = db.rawQuery("select * from ESTUDIANTES", null);

        // 3.- Ubicar nuestras columnas
        int ID = cursor_listar.getColumnIndex("ID");
        int NOMBRE = cursor_listar.getColumnIndex("NOMBRE");
        int APELLIDOS = cursor_listar.getColumnIndex("APELLIDOS");
        int EDAD = cursor_listar.getColumnIndex("EDAD");

        // 4.- RECORRER SELECT
        final ArrayList<Estudiante> lista_estudiantes = new ArrayList<Estudiante>();

        while (cursor_listar.moveToNext()) {
            Estudiante obj = new Estudiante();
            obj.ID = cursor_listar.getInt(ID);
            obj.NOMBRE = cursor_listar.getString(NOMBRE);
            obj.APELLIDOS = cursor_listar.getString(APELLIDOS);
            obj.EDAD = cursor_listar.getInt(EDAD);

            lista_estudiantes.add(obj);
        }

        // 5.- ADAPTADOR PERSONALIZADO
        ArrayAdapter<Estudiante> adapter = new ArrayAdapter<Estudiante>(this, R.layout.item_estudiante, lista_estudiantes) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_estudiante, parent, false);
                }

                Estudiante estudiante = lista_estudiantes.get(position);

                TextView item_id = convertView.findViewById(R.id.item_id);
                TextView item_nombre = convertView.findViewById(R.id.item_nombre);
                TextView item_edad = convertView.findViewById(R.id.item_edad);

                item_id.setText("ID: " + estudiante.ID);
                item_nombre.setText(estudiante.NOMBRE + " " + estudiante.APELLIDOS);
                item_edad.setText(estudiante.EDAD + " años");

                return convertView;
            }
        };

        // 6.- Declarar ListView
        ListView listViewDatos = findViewById(R.id.lista_estudiantes);
        listViewDatos.setAdapter(adapter);

        // boton agregar
        Button boton_agregar = findViewById(R.id.boton_agregar);
        boton_agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListarActivity.this, AgregarActivity.class);
                startActivity(intent);
            }
        });

        // Listview al hacer click ir a VerActivity
        listViewDatos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Estudiante objeto_actual = lista_estudiantes.get(position);
                Intent intent_detalle = new Intent(ListarActivity.this, VerActivity.class);
                intent_detalle.putExtra("ID", objeto_actual.ID);
                startActivity(intent_detalle);
            }
        });
    }
}