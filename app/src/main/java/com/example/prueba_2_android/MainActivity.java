package com.example.prueba_2_android;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // CREAR BASE DE DATOS Y TABLA EN EL ONCREATE
        SQLiteDatabase db = openOrCreateDatabase("BD_ESTUDIANTES", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS ESTUDIANTES (ID INTEGER PRIMARY KEY AUTOINCREMENT, NOMBRE VARCHAR, APELLIDOS VARCHAR, EDAD INTEGER)");

        // CREAR NUEVO HILO QUE ESPERA 5 SEGUNDOS Y REDIRIGE A ListarActivity
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Esperar 5 segundos
                    Thread.sleep(5000);

                    // Redirigir a ListarActivity
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(MainActivity.this, ListarActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}