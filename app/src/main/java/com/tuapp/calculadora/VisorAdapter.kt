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
        //
        val imageView: ImageView = view.findViewById(R.id.ivFotoFull)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_visor_foto, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val archivoCifrado = listaFotos[position]

        var fis: FileInputStream? = null
        try {
            fis = FileInputStream(archivoCifrado)
            
            // 
            val bytesDescifrados = cryptoManager.decrypt(fis)
            
            // 
            val bitmap = BitmapFactory.decodeByteArray(bytesDescifrados, 0, bytesDescifrados.size)
            
            if (bitmap != null) {
                holder.imageView.setImageBitmap(bitmap)
            } else {
                holder.imageView.setImageResource(android.R.drawable.ic_menu_report_image)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            holder.imageView.setImageResource(android.R.drawable.ic_menu_report_image)
        } finally {
            // 
            try {
                fis?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun getItemCount(): Int = listaFotos.size
}
