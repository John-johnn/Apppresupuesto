package com.ejemplo.apppresupuesto.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ejemplo.apppresupuesto.R
import com.ejemplo.apppresupuesto.data.model.Transaccion

class TransaccionAdapter(
    private val transacciones: List<Transaccion>
) : RecyclerView.Adapter<TransaccionAdapter.TransaccionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransaccionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaccion, parent, false)
        return TransaccionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransaccionViewHolder, position: Int) {
        val transaccion = transacciones[position]
        holder.descripcionText.text = transaccion.descripcion
        holder.montoText.text = transaccion.monto.toString()
        holder.fechaText.text = transaccion.fecha
    }

    override fun getItemCount(): Int = transacciones.size

    class TransaccionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val descripcionText: TextView = itemView.findViewById(R.id.textDescripcion)
        val montoText: TextView = itemView.findViewById(R.id.textMonto)
        val fechaText: TextView = itemView.findViewById(R.id.textFecha)
    }
}