package com.example.appcomprayventa.Adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appcomprayventa.R
import com.example.appcomprayventa.databinding.ItemImagenSliderBinding

class AdaptadorSliderImagenes(
    private val context: Context,
    private val imagenes: ArrayList<String>
) : RecyclerView.Adapter<AdaptadorSliderImagenes.SliderViewHolder>() {

    inner class SliderViewHolder(val binding: ItemImagenSliderBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(url: String) {
            Glide.with(context)
                .load(url)
                .placeholder(R.drawable.item_imagen)
                .into(binding.imgSlider)   // 👈 YA NO USAMOS itemView.imgSlider
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val binding = ItemImagenSliderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SliderViewHolder(binding)
    }

    override fun getItemCount(): Int = imagenes.size

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.bind(imagenes[position])
    }
}
