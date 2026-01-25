package com.tuapp.calculadora

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

class DescargasAdapter(
    private val archivos: MutableList<File>,
    private val onClick: (File) -> Unit,
    private val onLongClick: (Int) -> Unit
) : RecyclerView.Adapter<DescargasAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tvNombreArchivo)
        val detalle: TextView = view.findViewById(R.id.tvDetalleArchivo)
        val icono: ImageView = view.findViewById(R.id.ivIconoArchivo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_descarga, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val archivo = archivos[position]
        
        holder.nombre.text = archivo.name
        holder.detalle.text = "${formatearTamaño(archivo.length())} • Toque para abrir"

        // --- Mejora en Lógica de Iconos ---
        val extension = archivo.extension.lowercase()
        val resIcono = when {
            extension in listOf("jpg", "jpeg", "png", "webp", "gif") -> android.R.drawable.ic_menu_gallery
            extension == "apk" -> android.R.drawable.sym_def_app_icon
            extension == "pdf" -> android.R.drawable.ic_menu_edit
            extension in listOf("mp4", "mkv", "avi", "mov") -> android.R.drawable.ic_media_play
            extension in listOf("zip", "rar", "7z") -> android.R.drawable.ic_menu_save
            else -> android.R.drawable.ic_input_get
        }
        holder.icono.setImageResource(resIcono)

        // Click normal: Abrir archivo
        holder.itemView.setOnClickListener {
            onClick(archivo)
        }
        
        // Click largo: Borrar archivo (Usando bindingAdapterPosition por seguridad)
        holder.itemView.setOnLongClickListener {
            val currentPos = holder.bindingAdapterPosition
            if (currentPos != RecyclerView.NO_POSITION) {
                onLongClick(currentPos)
            }
            true
        }
    }

    override fun getItemCount(): Int = archivos.size

    // Función de formateo limpia y eficiente
    private fun formatearTamaño(size: Long): String {
        if (size <= 0) return "0 B"
        val unidades = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitoGrupo = (log10(size.toDouble()) / log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(size / 1024.0.pow(digitoGrupo.toDouble())) + " " + unidades[digitoGrupo]
    }
}
