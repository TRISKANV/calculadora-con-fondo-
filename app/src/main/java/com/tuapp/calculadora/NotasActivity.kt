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

      // ... 

val prefs = getSharedPreferences("BovedaNotas", Context.MODE_PRIVATE)
val pinGuardado = prefs.getString("pin_notas", null) // Busca si ya existe un PIN

// Ca
if (pinGuardado == null) {
    btnEntrar.text = "Crear PIN de Seguridad"
    Toast.makeText(this, "Configura un PIN para tus notas", Toast.LENGTH_LONG).show()
} else {
    btnEntrar.text = "Desbloquear"
}

btnEntrar.setOnClickListener {
    val pinIngresado = etPin.text.toString()

    if (pinIngresado.isEmpty()) {
        Toast.makeText(this, "Ingresa un PIN", Toast.LENGTH_SHORT).show()
        return@setOnClickListener
    }

    if (pinGuardado == null) {
        // -
        prefs.edit().putString("pin_notas", pinIngresado).apply()
        Toast.makeText(this, "PIN guardado correctamente", Toast.LENGTH_SHORT).show()
        entrarANotas()
    } else {
        // --- P
        if (pinIngresado == pinGuardado) {
            entrarANotas()
        } else {
            Toast.makeText(this, "PIN Incorrecto", Toast.LENGTH_SHORT).show()
            etPin.text.clear()
        }
    }
}

// ...
private fun entrarANotas() {
    val layoutBloqueo = findViewById<LinearLayout>(R.id.layoutBloqueoNotas)
    val layoutContenido = findViewById<LinearLayout>(R.id.layoutContenidoNotas)
    val btnNuevaNota = findViewById<FloatingActionButton>(R.id.btnNuevaNota)
    
    layoutBloqueo.visibility = View.GONE
    layoutContenido.visibility = View.VISIBLE
    btnNuevaNota.visibility = View.VISIBLE
    cargarNotasDeDisco()
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
