package com.tuapp.calculadora

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.widget.GridView
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import kotlin.math.abs

class BovedaActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var gridView: GridView
    private lateinit var adaptador: FotoAdapter
    private val listaArchivos = mutableListOf<File>()
    
    // Sensores para el Modo Pánico
    private lateinit var sensorManager: SensorManager
    private var acelerometro: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boveda)

        // Inicializar Sensores
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        gridView = findViewById(R.id.gridViewFotos)
        val btnAgregar = findViewById<ImageButton>(R.id.btnAgregarFoto)

        cargarArchivosDesdeCarpeta()

        val seleccionarArchivoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                uri?.let { guardarArchivoEnCarpetaSecreta(it) }
            }
        }

        btnAgregar.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "*/*"
            val mimeTypes = arrayOf("image/*", "video/*")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            seleccionarArchivoLauncher.launch(intent)
        }

        gridView.setOnItemClickListener { _, _, position, _ ->
            val archivo = listaArchivos[position]
            if (archivo.extension.lowercase() == "mp4") {
                val intent = Intent(this, VideoActivity::class.java)
                intent.putExtra("ruta_video", archivo.absolutePath)
                startActivity(intent)
            } else {
                val intent = Intent(this, VisorActivity::class.java)
                intent.putExtra("ruta_imagen", archivo.absolutePath)
                startActivity(intent)
            }
        }
    }

    // LÓGICA DEL SENSOR (MODO PÁNICO)
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val z = event.values[2] // El eje Z detecta si está boca arriba o boca abajo

            // Si Z es menor a -8, significa que el celular está boca abajo
            if (z < -8.5) {
                ejecutarModoPanico()
            }
        }
    }

    private fun ejecutarModoPanico() {
        // Opción B: Cerrar todo y volver al HOME del celular
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
        finish() // Cierra la actividad para que no quede en segundo plano abierta
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onResume() {
        super.onResume()
        cargarArchivosDesdeCarpeta()
        // Registrar el sensor al volver a la app
        acelerometro?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        // Pausar el sensor para no gastar batería
        sensorManager.unregisterListener(this)
    }

    private fun cargarArchivosDesdeCarpeta() {
        val carpeta = File(getExternalFilesDir(null), "mis_secretos")
        if (!carpeta.exists()) carpeta.mkdirs()

        val archivos = carpeta.listFiles()
        listaArchivos.clear()
        if (archivos != null) {
            listaArchivos.addAll(archivos.filter { it.isFile }.sortedByDescending { it.lastModified() })
        }

        adaptador = FotoAdapter(this, listaArchivos)
        gridView.adapter = adaptador
    }

    private fun guardarArchivoEnCarpetaSecreta(uri: Uri) {
        try {
            val tipoMime = contentResolver.getType(uri)
            val extension = if (tipoMime?.contains("video") == true) "mp4" else "jpg"
            val inputStream = contentResolver.openInputStream(uri)
            val carpeta = File(getExternalFilesDir(null), "mis_secretos")
            val nombreArchivo = "FILE_${System.currentTimeMillis()}.$extension"
            val archivoDestino = File(carpeta, nombreArchivo)

            val outputStream = FileOutputStream(archivoDestino)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            Toast.makeText(this, "Guardado en la bóveda", Toast.LENGTH_SHORT).show()
            cargarArchivosDesdeCarpeta()
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
