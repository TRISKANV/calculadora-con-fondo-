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

        // 1. Inicializar Sensores (Modo Pánico)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // 2. Referencias de los componentes de la interfaz
        gridView = findViewById(R.id.gridViewFotos)
        val btnAgregar = findViewById<ImageButton>(R.id.btnAgregarFoto)
        val btnNavegador = findViewById<ImageButton>(R.id.btnNavegador) // BOTÓN MUNDO

        // 3. Cargar las fotos que ya existan
        cargarArchivosDesdeCarpeta()

        // 4. Lógica para elegir fotos/videos del celular
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

        // 5. CONFIGURAR BOTÓN DEL NAVEGADOR (EL MUNDITO)
        btnNavegador.setOnClickListener {
            val intent = Intent(this, NavegadorActivity::class.java)
            startActivity(intent)
        }

        // 6. Al tocar una foto, abrir el visor o el video
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

    // --- LÓGICA DEL SENSOR (MODO PÁNICO) ---
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val z = event.values[2]
            if (z < -8.5) { // Si el celular está boca abajo
                ejecutarModoPanico()
            }
        }
    }

    private fun ejecutarModoPanico() {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
        finish() 
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onResume() {
        super.onResume()
        cargarArchivosDesdeCarpeta()
        acelerometro?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    // --- MANEJO DE ARCHIVOS ---
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
