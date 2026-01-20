package com.tuapp.calculadora

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NotasActivity : AppCompatActivity() {

    private lateinit var rvNotas: RecyclerView
    private var listaNotas = mutableListOf<Nota>()
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notas)

        val layoutBloqueo = findViewById<LinearLayout>(R.id.layoutBloqueoNotas)
        val layoutContenido = findViewById<LinearLayout>(R.id.layoutContenidoNotas)
        val etPin = findViewById<EditText>(R.id.etPinNotas)
        val btnEntrar = findViewById<Button>(R.id.btnEntrarNotas)
        val btnNuevaNota = findViewById<FloatingActionButton>(R.id.btnNuevaNota)

        rvNotas = findViewById(R.id.rvNotas)
        rvNotas.layoutManager = LinearLayoutManager(this)

        // Lógica de desbloqueo
        btnEntrar.setOnClickListener {
            if (etPin.text.toString() == "1234") {
                layoutBloqueo.visibility = View.GONE
                layoutContenido.visibility = View.VISIBLE
                btnNuevaNota.visibility = View.VISIBLE
                cargarNotasDeDisco()
            } else {
                Toast.makeText(this, "PIN Incorrecto", Toast.LENGTH_SHORT).show()
            }
        }

        btnNuevaNota.setOnClickListener {
            mostrarEditor(null)
        }
    }

    private fun cargarNotasDeDisco() {
        val prefs = getSharedPreferences("BovedaNotas", Context.MODE_PRIVATE)
        val json = prefs.getString("lista_notas_pro", null)
        if (json != null) {
            val type = object : TypeToken<MutableList<Nota>>() {}.type
            listaNotas = gson.fromJson(json, type)
        }
        actualizarVista()
    }

    private fun actualizarVista() {
        rvNotas.adapter = NotasAdapter(listaNotas) { nota ->
            mostrarEditor(nota)
        }
    }

    private fun mostrarEditor(notaExistente: Nota?) {
        val builder = AlertDialog.Builder(this)
        val input = EditText(this)
        input.setText(notaExistente?.contenido ?: "")
        input.hint = "Escribe tu nota aquí..."
        input.setPadding(50, 40, 50, 40)

        builder.setTitle(if (notaExistente == null) "Nueva Nota" else "Editar Nota")
        builder.setView(input)

        builder.setPositiveButton("Guardar") { _, _ ->
            val texto = input.text.toString()
            if (texto.isNotEmpty()) {
                if (notaExistente == null) {
                    listaNotas.add(Nota(contenido = texto))
                } else {
                    notaExistente.contenido = texto
                }
                guardarEnDisco()
            }
        }

        builder.setNegativeButton("Cancelar", null)

        if (notaExistente != null) {
            builder.setNeutralButton("Eliminar") { _, _ ->
                listaNotas.remove(notaExistente)
                guardarEnDisco()
            }
        }
        builder.show()
    }

    private fun guardarEnDisco() {
        val prefs = getSharedPreferences("BovedaNotas", Context.MODE_PRIVATE)
        val json = gson.toJson(listaNotas)
        prefs.edit().putString("lista_notas_pro", json).apply()
        actualizarVista()
    }
}
