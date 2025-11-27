package com.example.appcomprayventa.Anuncios

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.appcomprayventa.databinding.ActivityDetalleAnuncioBinding
import com.google.firebase.database.FirebaseDatabase

class DetalleAnuncio : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleAnuncioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleAnuncioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idAnuncio = intent.getStringExtra("idAnuncio") ?: return
        cargarDetalle(idAnuncio)

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun cargarDetalle(id: String) {
        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")

        ref.child(id).get().addOnSuccessListener { snapshot ->

            val titulo = snapshot.child("titulo").value.toString()
            val precio = snapshot.child("precio").value.toString()
            val descripcion = snapshot.child("descripcion").value.toString()
            val categoria = snapshot.child("categoria").value.toString()
            val estado = snapshot.child("estado").value.toString()

            binding.tvTitulo.text = titulo
            binding.tvPrecio.text = "$$precio"
            binding.tvDescripcion.text = descripcion
            binding.tvCategoria.text = categoria
            binding.tvEstado.text = estado

            val imagenes = snapshot.child("Imagenes")
            for (img in imagenes.children) {
                val url = img.child("imagenUrl").value.toString()
                if (url.isNotEmpty()) {
                    Glide.with(this).load(url).into(binding.imgPrincipal)
                    break
                }
            }
        }
    }
}
