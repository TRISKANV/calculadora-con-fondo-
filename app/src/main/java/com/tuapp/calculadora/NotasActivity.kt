package com.tuapp.calculadora

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class NotasActivity : AppCompatActivity() {

    private lateinit var etNotas: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notas)

        etNotas = findViewById(R.id.etNotasPrivadas)
        val btnGuardar = findViewById<ImageButton>(R.id.btnGuardarNota)

        // 1. Cargar la nota guardada anteriormente
        val prefs = getSharedPreferences("BovedaNotas", Context.MODE_PRIVATE)
        val notaGuardada = prefs.getString("mi_nota", "")
        etNotas.setText(notaGuardada)

        // 2.
        btnGuardar.setOnClickListener {
            val texto = etNotas.text.toString()
            prefs.edit().putString("mi_nota", texto).apply()
            Toast.makeText(this, "Nota guardada con éxito", Toast.LENGTH_SHORT).show()
            finish() // Cr
        }
    }

    // 3. 
    override fun onPause() {
        super.onPause()
        val prefs = getSharedPreferences("BovedaNotas", Context.MODE_PRIVATE)
        prefs.edit().putString("mi_nota", etNotas.text.toString()).apply()
    }
}
