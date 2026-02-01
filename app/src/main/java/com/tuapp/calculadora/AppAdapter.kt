package com.tuapp.calculadora

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView

class AppAdapter(private val listaApps: List<AppInfo>) : RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

    class AppViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivIcono: ImageView = view.findViewById(R.id.ivAppIcon)
        val tvNombre: TextView = view.findViewById(R.id.tvAppName)
        val swBloqueo: SwitchCompat = view.findViewById(R.id.switchLock)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_app, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = listaApps[position]
        holder.tvNombre.text = app.nombre
        holder.ivIcono.setImageDrawable(app.icono)
        holder.swBloqueo.isChecked = app.estaBloqueada

        // 
        holder.swBloqueo.setOnCheckedChangeListener { _, isChecked ->
            app.estaBloqueada = isChecked
        }
    }

    override fun getItemCount() = listaApps.size
}
