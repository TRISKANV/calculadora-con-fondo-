package com.tuapp.calculadora

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class GaleriaAdapter(
    private var listaFotos: MutableList<File>,
    private val onFotoClick: (Int) -> Unit,
    private val onFotoDelete: (Int) -> Unit
) : RecyclerView.Adapter<GaleriaAdapter.FotoViewHolder>() {

    class FotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivThumbnail: ImageView = view.findViewById(R.id.ivThumbnail)
        val btnBorrar: ImageButton = view.findViewById(R.id.btnBorrarFoto) // Agregaremos este ID al XML
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_foto, parent, false)
        return FotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: FotoViewHolder, position: Int) {
        val fotoFile = listaFotos[position]
        
        // Cargar miniatura
        holder.ivThumbnail.setImageURI(android.net.Uri.fromFile(fotoFile))
        
        // Click para ver en grande
        holder.ivThumbnail.setOnClickListener { onFotoClick(position) }
        
        // BOTÓN BORRAR REPARADO
        holder.btnBorrar.setOnClickListener {
            onFotoDelete(holder.adapterPosition) 
        }
    }

    override fun getItemCount() = listaFotos.size

    // Método para actualizar la lista sin errores
    fun actualizarLista(nuevaLista: MutableList<File>) {
        this.listaFotos = nuevaLista
        notifyDataSetChanged()
    }
}
