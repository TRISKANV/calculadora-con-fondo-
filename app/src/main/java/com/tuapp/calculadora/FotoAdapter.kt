package com.tuapp.calculadora

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import java.io.File

class FotoAdapter(private val context: Context, private val listaFotos: List<File>) : BaseAdapter() {

    override fun getCount(): Int = listaFotos.size
    override fun getItem(position: Int): Any = listaFotos[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_foto, parent, false)
        val imageView = view.findViewById<ImageView>(R.id.ivItemFoto)
        
        val archivo = listaFotos[position]
        imageView.setImageURI(Uri.fromFile(archivo))

        // Al tocar la foto, enviamos la ruta a VisorActivity
        imageView.setOnClickListener {
            val intent = Intent(context, VisorActivity::class.java)
            intent.putExtra("ruta_imagen", archivo.absolutePath)
            context.startActivity(intent)
        }
        
        return view
    }
}
