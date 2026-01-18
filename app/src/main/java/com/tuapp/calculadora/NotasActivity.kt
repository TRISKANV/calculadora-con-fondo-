package com.tuapp.calculadora

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NotasActivity : AppCompatActivity() {

    private lateinit var rvNotas: RecyclerView
    private lateinit var adapter: NotasAdapter
    private var listaNotas = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notas)

        rvNotas = findViewById(R.id.rvNotas)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAddNota)

        rvNotas.layoutManager = LinearLayoutManager(this)
        cargarNotas()

        fabAdd.setOnClickListener {
            mostrarDialogoNuevaNota()
        }
    }

    private fun cargarNotas() {
        val prefs = getSharedPreferences("NotasEncriptadas", Context.MODE_PRIVATE)
        val notasSet = prefs.getStringSet("mis_notas", mutableSetOf()) ?: mutableSetOf()
        listaNotas = notasSet.toMutableList()
        adapter = NotasAdapter(listaNotas)
        rvNotas.adapter = adapter
    }

    private fun mostrarDialogoNuevaNota() {
        val input = EditText(this)
        input.setPadding(50, 40, 50, 40)
        
        AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
            .setTitle("Nueva Nota Privada")
            .setView(input)
            .setPositiveButton("Guardar") { _, _ ->
                val texto = input.text.toString()
                if (texto.isNotEmpty()) {
                    guardarNota(texto)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun guardarNota(texto: String) {
        val prefs = getSharedPreferences("NotasEncriptadas", Context.MODE_PRIVATE)
        listaNotas.add(texto)
        prefs.edit().putStringSet("mis_notas", listaNotas.toSet()).apply()
        adapter.notifyDataSetChanged() // Refresca la lista al instante
    }
}
