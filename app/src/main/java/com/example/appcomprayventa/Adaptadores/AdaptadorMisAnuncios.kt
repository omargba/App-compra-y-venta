package com.example.appcomprayventa.Adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appcomprayventa.Modelo.ModeloAnuncio
import com.example.appcomprayventa.R

class AdaptadorMisAnuncios(
    private val context: Context,
    private val anunciosArrayList: ArrayList<ModeloAnuncio>,
    private val listener: ((ModeloAnuncio) -> Unit)? = null
) : RecyclerView.Adapter<AdaptadorMisAnuncios.HolderAnuncio>() {

    inner class HolderAnuncio(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgAnuncio: ImageView = itemView.findViewById(R.id.imgAnuncio)
        val tvTitulo: TextView = itemView.findViewById(R.id.tvTitulo)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        val tvCategoria: TextView = itemView.findViewById(R.id.tvCategoria)
        val tvEstado: TextView = itemView.findViewById(R.id.tvEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderAnuncio {
        val view = LayoutInflater.from(context).inflate(R.layout.item_mi_anuncio, parent, false)
        return HolderAnuncio(view)
    }

    override fun onBindViewHolder(holder: HolderAnuncio, position: Int) {
        val modelo = anunciosArrayList[position]

        holder.tvTitulo.text = modelo.titulo
        holder.tvPrecio.text = "$${modelo.precio}"
        holder.tvCategoria.text = modelo.categoria
        holder.tvEstado.text = modelo.estado

        val imgUrl = modelo.primeraImagenUrl
        if (!imgUrl.isNullOrEmpty()) {
            Glide.with(context)
                .load(imgUrl)
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.imgAnuncio)
        } else {
            holder.imgAnuncio.setImageResource(R.mipmap.ic_launcher)
        }

        holder.itemView.setOnClickListener {
            listener?.invoke(modelo)
        }
    }

    override fun getItemCount(): Int = anunciosArrayList.size
}
