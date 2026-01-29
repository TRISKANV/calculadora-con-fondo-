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
        val imageView: ImageView = view.findViewById(R.id.ivFotoFull)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_visor_foto, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val archivoCifrado = listaFotos[position]

        try {
            // 
            val inputStream = FileInputStream(archivoCifrado)
            
            // 
            val bytesDescifrados = cryptoManager.decrypt(inputStream)
            
            // 3
            val bitmap = BitmapFactory.decodeByteArray(bytesDescifrados, 0, bytesDescifrados.size)
            
            //
            holder.imageView.setImageBitmap(bitmap)
            
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
            // 
        }
    }

    override fun getItemCount(): Int = listaFotos.size
}
