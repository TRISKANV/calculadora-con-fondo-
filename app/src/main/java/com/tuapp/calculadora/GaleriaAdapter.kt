package com.tuapp.calculadora

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileInputStream

class GaleriaAdapter(
    private val listaFotos: MutableList<File>,
    private val onFotoClick: (Int) -> Unit,
    private val onFotoDelete: (Int) -> Unit
) : RecyclerView.Adapter<GaleriaAdapter.ViewHolder>() {

    private val cryptoManager = CryptoManager()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Estos IDs deben estar en tu item_foto.xml
        val ivThumbnail: ImageView = view.findViewById(R.id.ivThumbnail)
        val btnDelete: ImageButton = view.findViewById(R.id.btnBorrarFoto) 
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // CORREGIDO: item_foto (singular) para que coincida con tu XML
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_foto, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val archivoCifrado = listaFotos[position]

        try {
            val inputStream = FileInputStream(archivoCifrado)
            val bytesDescifrados = cryptoManager.decrypt(inputStream)
            
            // Decodificamos con sampleSize para no matar la memoria RAM
            val opciones = BitmapFactory.Options().apply {
                inSampleSize = 4 
            }
            
            val bitmap = BitmapFactory.decodeByteArray(bytesDescifrados, 0, bytesDescifrados.size, opciones)
            holder.ivThumbnail.setImageBitmap(bitmap)
            
            inputStream.close()
        } catch (e: Exception) {
            // Si el descifrado falla, mostramos el icono de error de Android
            holder.ivThumbnail.setImageResource(android.R.drawable.ic_menu_report_image)
        }

        holder.itemView.setOnClickListener { onFotoClick(position) }
        holder.btnDelete.setOnClickListener { onFotoDelete(position) }
    }

    override fun getItemCount(): Int = listaFotos.size
}
