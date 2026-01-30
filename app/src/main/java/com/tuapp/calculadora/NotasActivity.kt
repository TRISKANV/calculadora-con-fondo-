package com.tuapp.calculadora

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
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

        // --- SEGURIDAD: Bloqueo de capturas y ocultar contenido en multitarea ---
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        setContentView(R.layout.activity_notas)

        // Referencias del diseño
        val layoutBloqueo = findViewById<LinearLayout>(R.id.layoutBloqueoNotas)
        val layoutContenido = findViewById<LinearLayout>(R.id.layoutContenidoNotas)
        val etPin = findViewById<EditText>(R.id.etPinNotas)
        val btnEntrar = findViewById<Button>(R.id.btnEntrarNotas)
        val btnNuevaNota = findViewById<FloatingActionButton>(R.id.btnNuevaNota)
        val tvTitulo = findViewById<TextView>(R.id.tvTituloBloqueo)

        rvNotas = findViewById(R.id.rvNotas)
        rvNotas.layoutManager = LinearLayoutManager(this)

        val prefs = getSharedPreferences("BovedaNotas", Context.MODE_PRIVATE)
        val pinGuardado = prefs.getString("pin_notas", null)

        // Ajustar textos según si ya existe un PIN
        if (pinGuardado == null) {
            tvTitulo.text = "Configurar PIN de Notas"
            btnEntrar.text = "Guardar y Entrar"
        } else {
            tvTitulo.text = "Bóveda de Notas"
            btnEntrar.text = "Desbloquear"
        }

        btnEntrar.setOnClickListener {
            val pinIngresado = etPin.text.toString()

            if (pinIngresado.isEmpty()) {
                Toast.makeText(this, "Ingresa un PIN", Toast.LENGTH_SHORT).show()
            } else {
                if (pinGuardado == null) {
                    prefs.edit().putString("pin_notas", pinIngresado).apply()
                    Toast.makeText(this, "PIN configurado", Toast.LENGTH_SHORT).show()
                    entrarANotas(layoutBloqueo, layoutContenido, btnNuevaNota)
                } else if (pinIngresado == pinGuardado) {
                    entrarANotas(layoutBloqueo, layoutContenido, btnNuevaNota)
                } else {
                    Toast.makeText(this, "PIN Incorrecto", Toast.LENGTH_SHORT).show()
                    etPin.text.clear()
                }
            }
        }

        btnNuevaNota.setOnClickListener {
            mostrarEditor(null)
        }
    }

    /**
     * BLINDAJE: Si el usuario sale de la app, cerramos la actividad.
     * Esto obliga a pasar por la calculadora Y por el PIN de notas de nuevo.
     */
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        finish()
    }

    private fun entrarANotas(bloqueo: View, contenido: View, btn: View) {
        bloqueo.visibility = View.GONE
        contenido.visibility = View.VISIBLE
        btn.visibility = View.VISIBLE
        cargarNotasDeDisco()
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
        val builder = AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
        val input = EditText(this)
        input.setText(notaExistente?.contenido ?: "")
        input.setTextColor(android.graphics.Color.WHITE)
        input.hint = "Escribe tu nota aquí..."
        input.setHintTextColor(android.graphics.Color.GRAY)
        input.setPadding(60, 40, 60, 40)

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

    override fun onBackPressed() {
        // Al presionar atrás, cerramos la actividad por completo
        super.onBackPressed()
        finish()
    }
}
