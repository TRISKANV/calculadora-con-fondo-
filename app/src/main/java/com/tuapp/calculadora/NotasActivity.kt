package com.tuapp.calculadora

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class NotasActivity : AppCompatActivity() {

    private lateinit var etNotas: EditText
    private lateinit var layoutBloqueo: LinearLayout
    private lateinit var layoutContenido: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notas)

        etNotas = findViewById(R.id.etNotasPrivadas)
        layoutBloqueo = findViewById(R.id.layoutBloqueoNotas)
        layoutContenido = findViewById(R.id.layoutContenidoNotas)
        
        val etPin = findViewById<EditText>(R.id.etPinNotas)
        val btnEntrar = findViewById<Button>(R.id.btnEntrarNotas)

        // Lógica de Contraseña Extra
        btnEntrar.setOnClickListener {
            if (etPin.text.toString() == "1234") { // <--- Cambia aquí tu clave extra
                layoutBloqueo.visibility = View.GONE
                layoutContenido.visibility = View.VISIBLE
                cargarNota()
            } else {
                Toast.makeText(this, "PIN Incorrecto", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<ImageButton>(R.id.btnGuardarNota).setOnClickListener { guardarNota() }
        findViewById<ImageButton>(R.id.btnEliminarNota).setOnClickListener { confirmarEliminacion() }
    }

    private fun cargarNota() {
        val prefs = getSharedPreferences("BovedaNotas", Context.MODE_PRIVATE)
        etNotas.setText(prefs.getString("mi_nota", ""))
    }

    private fun guardarNota() {
        val prefs = getSharedPreferences("BovedaNotas", Context.MODE_PRIVATE)
        prefs.edit().putString("mi_nota", etNotas.text.toString()).apply()
        Toast.makeText(this, "Guardado", Toast.LENGTH_SHORT).show()
    }

    private fun confirmarEliminacion() {
        AlertDialog.Builder(this)
            .setTitle("¿Eliminar nota?")
            .setMessage("Esto borrará todo el texto de esta nota.")
            .setPositiveButton("Eliminar") { _, _ ->
                etNotas.setText("")
                guardarNota()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
