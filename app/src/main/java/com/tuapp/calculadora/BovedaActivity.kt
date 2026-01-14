package com.tuapp.calculadora

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class BovedaActivity : AppCompatActivity() {

    private val PICK_IMAGE_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boveda)

        val btnAgregar = findViewById<FloatingActionButton>(R.id.btnAgregar)

        btnAgregar.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_CODE) {
            val imageUri = data?.data
            // Por ahora solo mostraremos un mensaje, 
            // en el siguiente paso la guardaremos físicamente.
            Toast.makeText(this, "Foto seleccionada correctamente", Toast.LENGTH_SHORT).show()
        }
    }
}
