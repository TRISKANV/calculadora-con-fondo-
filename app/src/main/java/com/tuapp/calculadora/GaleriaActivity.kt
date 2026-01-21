package com.tuapp.calculadora

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_galeria)

        rvGaleria = findViewById(R.id.rvGaleria)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAddFoto)

        rvGaleria.layoutManager = GridLayoutManager(this, 3)
        
        cargarFotosDesdeBoveda()

        // Registro del selector de archivos moderno
        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    try {
                        // Solicitamos permiso persistente para que Android no nos corte el acceso
                        contentResolver.takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                        importarFotoABoveda(uri)
                    } catch (e: Exception) {
                        // Si el selector no soporta persistencia, intentamos importar igual
                        importarFotoABoveda(uri)
                    }
                }
            }
        }

        fabAdd.setOnClickListener {
            // Usamos ACTION_OPEN_DOCUMENT en lugar de ACTION_PICK para evitar errores de seguridad
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            launcher.launch(intent)
        }
    }

    private fun cargarFotosDesdeBoveda() {
        val carpetaPrivada = File(getExternalFilesDir(null), "FotosSecretas")
        if (!carpetaPrivada.exists()) carpetaPrivada.mkdirs()
        
        val archivosEnCarpeta = carpetaPrivada.listFiles()?.filter { it.isFile }?.toMutableList() ?: mutableListOf()
        
        listaFotos.clear()
        listaFotos.addAll(archivosEnCarpeta)
        
        if (::adapter.isInitialized) {
            adapter.notifyDataSetChanged()
        } else {
            adapter = GaleriaAdapter(listaFotos, 
                onFotoClick = { posicion ->
                    val intent = Intent(this, VisorActivity::class.java)
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

    private fun importarFotoABoveda(uriOriginal: Uri) {
        try {
            val nombreArchivo = "SECRET_${System.currentTimeMillis()}.jpg"
            val carpetaPrivada = File(getExternalFilesDir(null), "FotosSecretas")
            if (!carpetaPrivada.exists()) carpetaPrivada.mkdirs()
            val archivoDestino = File(carpetaPrivada, nombreArchivo)

            // PROCESO DE COPIADO
            contentResolver.openInputStream(uriOriginal)?.use { inputStream ->
                FileOutputStream(archivoDestino).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            // PROCESO DE BORRADO DEL ORIGINAL
            // En Android 11+ aparecerá un cuadro de diálogo pidiendo permiso para borrar
            try {
                contentResolver.delete(uriOriginal, null, null)
            } catch (e: Exception) {
                // Si falla por seguridad, el usuario deberá borrarla manualmente o
                // el sistema lanzará automáticamente la solicitud de borrado.
            }

            Toast.makeText(this, "Foto protegida con éxito", Toast.LENGTH_SHORT).show()
            cargarFotosDesdeBoveda()
            
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error de acceso: No se pudo guardar la imagen", Toast.LENGTH_LONG).show()
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
}
