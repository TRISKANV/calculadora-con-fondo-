package com.tuapp.calculadora

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class NotasAdapter(
    private val notas: List<Nota>,
    private val onClick: (Nota) -> Unit
) : RecyclerView.Adapter<NotasAdapter.NotaViewHolder>() {

    class NotaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Usamos tus IDs exactos
        val tvContenido: TextView = view.findViewById(R.id.tvContenidoNota)
        val tvFecha: TextView = view.findViewById(R.id.tvFechaNota)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_nota, parent, false)
        return NotaViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotaViewHolder, position: Int) {
        val nota = notas[position]
        
        // Seteamos el texto
        holder.tvContenido.text = nota.contenido
        
        // Formateamos la fecha (ej: 12 Oct, 10:30 PM)
        val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        holder.tvFecha.text = sdf.format(Date(nota.fecha))

        holder.itemView.setOnClickListener { onClick(nota) }
    }

    override fun getItemCount() = notas.size
}
