package com.tuapp.calculadora

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NotasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notas)

        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAddNota)

        fabAdd.setOnClickListener {
            mostrarDialogoNuevaNota()
        }
    }

    private fun mostrarDialogoNuevaNota() {
        val input = EditText(this)
        input.hint = "Escribe tu secreto aquí..."
        
        AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
            .setTitle("Nueva Nota Privada")
            .setView(input)
            .setPositiveButton("Guardar") { _, _ ->
                val nota = input.text.toString()
                if (nota.isNotEmpty()) {
                    guardarNota(nota)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun guardarNota(texto: String) {
        val prefs = getSharedPreferences("NotasEncriptadas", Context.MODE_PRIVATE)
        val notasExistentes = prefs.getStringSet("mis_notas", mutableSetOf()) ?: mutableSetOf()
        val nuevasNotas = notasExistentes.toMutableSet()
        nuevasNotas.add(texto)
        prefs.edit().putStringSet("mis_notas", nuevasNotas).apply()
        // Aquí luego agregaremos la lógica para refrescar la lista en pantalla
    }
}
