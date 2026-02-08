package com.triskanv.calculadora

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

        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        setContentView(R.layout.activity_galeria)

        rvGaleria = findViewById(R.id.rvGaleria)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAddFoto)

        rvGaleria.layoutManager = GridLayoutManager(this, 3)
        
        cargarFotosDesdeBoveda()

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
     * 
     */
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        // finish() 
    }

    private fun cargarFotosDesdeBoveda() {
        // 
        val carpetaPrivada = File(getExternalFilesDir(null), "FotosSecretas")
        if (!carpetaPrivada.exists()) carpetaPrivada.mkdirs()
        
        // 
        val extensionesPermitidas = listOf("enc", "jpg", "jpeg", "png", "webp")
        
        val archivosEnCarpeta = carpetaPrivada.listFiles()
            ?.filter { archivo -> 
                archivo.isFile && extensionesPermitidas.any { ext -> archivo.name.lowercase().endsWith(ext) } 
            }
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
            val archivoDestino = File(carpetaPrivada, nombreArchivo)

            contentResolver.openInputStream(uriOriginal)?.use { inputStream ->
                FileOutputStream(archivoDestino).use { outputStream ->
                    cryptoManager.encrypt(inputStream, outputStream)
                }
            }

            Toast.makeText(this, "Foto protegida ✅", Toast.LENGTH_SHORT).show()
            cargarFotosDesdeBoveda()
            
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al cifrar", Toast.LENGTH_LONG).show()
        }
    }

    private fun borrarFotoDeBoveda(posicion: Int) {
        if (posicion !in listaFotos.indices) return
        val archivoABorrar = listaFotos[posicion]
        if (archivoABorrar.exists() && archivoABorrar.delete()) {
            listaFotos.removeAt(posicion)
            adapter.notifyItemRemoved(posicion)
            adapter.notifyItemRangeChanged(posicion, listaFotos.size)
            Toast.makeText(this, "Eliminado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
