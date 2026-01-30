package com.tuapp.calculadora

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileOutputStream

class GaleriaActivity : AppCompatActivity() {

    private lateinit var rvGaleria: RecyclerView
    private lateinit var adapter: GaleriaAdapter
    private var listaFotos = mutableListOf<File>()
    private val cryptoManager = CryptoManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // SEGURIDAD: Bloqueo de capturas y ocultar contenido en recientes
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        setContentView(R.layout.activity_galeria)

        rvGaleria = findViewById(R.id.rvGaleria)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAddFoto)

        // Diseño de grilla (3 columnas)
        rvGaleria.layoutManager = GridLayoutManager(this, 3)
        
        cargarFotosDesdeBoveda()

        // Configuración del selector de imágenes
        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                uri?.let { importarYCifrarFoto(it) }
            }
        }

        fabAdd.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            launcher.launch(intent)
        }
    }

    /**
     * BLINDAJE: Si el usuario presiona el botón Home o Recientes,
     * la galería se cierra para evitar que alguien vea las miniaturas después.
     */
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        finish()
    }

    private fun cargarFotosDesdeBoveda() {
        val carpetaPrivada = File(getExternalFilesDir(null), "FotosSecretas")
        if (!carpetaPrivada.exists()) carpetaPrivada.mkdirs()
        
        val archivosEnCarpeta = carpetaPrivada.listFiles()
            ?.filter { it.isFile && it.name.endsWith(".enc") }
            ?.sortedByDescending { it.lastModified() }
            ?.toMutableList() ?: mutableListOf()
        
        listaFotos.clear()
        listaFotos.addAll(archivosEnCarpeta)
        
        if (::adapter.isInitialized) {
            adapter.notifyDataSetChanged()
        } else {
            adapter = GaleriaAdapter(listaFotos, 
                onFotoClick = { posicion ->
                    val intent = Intent(this, VisorActivity::class.java)
                    // Pasamos la lista de rutas para que el visor pueda hacer swipe (si lo configuraste)
                    val rutas = listaFotos.map { it.absolutePath }.toTypedArray()
                    intent.putExtra("lista_fotos", rutas)
                    intent.putExtra("posicion", posicion)
                    startActivity(intent)
                },
                onFotoDelete = { posicion ->
                    borrarFotoDeBoveda(posicion)
                }
            )
            rvGaleria.adapter = adapter
        }
    }

    private fun importarYCifrarFoto(uriOriginal: Uri) {
        try {
            val nombreArchivo = "IMG_${System.currentTimeMillis()}.enc"
            val carpetaPrivada = File(getExternalFilesDir(null), "FotosSecretas")
            if (!carpetaPrivada.exists()) carpetaPrivada.mkdirs()
            
            val archivoDestino = File(carpetaPrivada, nombreArchivo)

            // Usamos el CryptoManager para el proceso de cifrado
            contentResolver.openInputStream(uriOriginal)?.use { inputStream ->
                FileOutputStream(archivoDestino).use { outputStream ->
                    cryptoManager.encrypt(inputStream, outputStream)
                }
            }

            Toast.makeText(this, "Foto protegida ✅", Toast.LENGTH_SHORT).show()
            cargarFotosDesdeBoveda()
            
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al cifrar la imagen", Toast.LENGTH_LONG).show()
        }
    }

    private fun borrarFotoDeBoveda(posicion: Int) {
        if (posicion !in listaFotos.indices) return
        val archivoABorrar = listaFotos[posicion]
        
        if (archivoABorrar.exists()) {
            if (archivoABorrar.delete()) {
                listaFotos.removeAt(posicion)
                adapter.notifyItemRemoved(posicion)
                adapter.notifyItemRangeChanged(posicion, listaFotos.size)
                Toast.makeText(this, "Eliminado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
