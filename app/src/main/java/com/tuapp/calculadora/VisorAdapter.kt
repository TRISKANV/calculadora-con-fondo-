package com.tuapp.calculadora

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileInputStream

class VisorAdapter(private val listaFotos: List<File>) : 
    RecyclerView.Adapter<VisorAdapter.ViewHolder>() {

    private val cryptoManager = CryptoManager()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Este ID DEBE existir en item_visor_foto.xml
        val imageView: ImageView = view.findViewById(R.id.ivFotoFull)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Asegúrate de que el archivo XML se llame exactamente item_visor_foto.xml
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_visor_foto, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val archivoCifrado = listaFotos[position]

        var fis: FileInputStream? = null
        try {
            fis = FileInputStream(archivoCifrado)
            
            // Desciframos la imagen usando el CryptoManager
            val bytesDescifrados = cryptoManager.decrypt(fis)
            
            // Decodificamos el array de bytes a un Bitmap
            val bitmap = BitmapFactory.decodeByteArray(bytesDescifrados, 0, bytesDescifrados.size)
            
            if (bitmap != null) {
                holder.imageView.setImageBitmap(bitmap)
            } else {
                // Si el bitmap es nulo (error de descifrado o archivo corrupto)
                holder.imageView.setImageResource(android.R.drawable.ic_menu_report_image)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            // Mostramos icono de error si algo falla
            holder.imageView.setImageResource(android.R.drawable.ic_menu_report_image)
        } finally {
            // Cerramos el flujo para evitar fugas de memoria (Memory Leaks)
            try {
                fis?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun getItemCount(): Int = listaFotos.size
}
