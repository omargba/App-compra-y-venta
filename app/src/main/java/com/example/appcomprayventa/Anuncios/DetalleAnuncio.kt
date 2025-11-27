package com.example.appcomprayventa.Anuncios

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.appcomprayventa.databinding.ActivityDetalleAnuncioBinding
import com.google.firebase.database.*

class DetalleAnuncio : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleAnuncioBinding
    private lateinit var idAnuncio: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleAnuncioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idAnuncio = intent.getStringExtra("idAnuncio") ?: return

        cargarDetalle()
        aumentarVisita()

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnVendido.setOnClickListener {
            marcarComoVendido()
        }

        binding.btnEliminar.setOnClickListener {
            eliminarAnuncio()
        }
    }

    // ----------------------------------------------------------
    // CARGAR DETALLES
    // ----------------------------------------------------------
    private fun cargarDetalle() {
        val ref = FirebaseDatabase.getInstance().getReference("Anuncios").child(idAnuncio)

        ref.get().addOnSuccessListener { snapshot ->

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

            // 🔥 CAMBIAR TEXTO DEL BOTÓN AUTOMÁTICAMENTE
            if (estado == "Vendido") {
                binding.btnVendido.text = "Marcar como Disponible"
            } else {
                binding.btnVendido.text = "Marcar como Vendido"
            }

            // Visitas
            val visitas = snapshot.child("contadorVistas").value?.toString() ?: "0"
            binding.tvVisitas.text = "👁 $visitas visitas"

            // Imagen principal
            var urlImg = ""
            val imagenes = snapshot.child("Imagenes")
            for (img in imagenes.children) {
                urlImg = img.child("imagenUrl").value.toString()
                if (urlImg.isNotEmpty()) break
            }

            Glide.with(this)
                .load(urlImg)
                .into(binding.imgPrincipal)
        }
    }


    // ----------------------------------------------------------
    // AUMENTAR VISITA
    // ----------------------------------------------------------
    private fun aumentarVisita() {
        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
            .child(idAnuncio)
            .child("contadorVistas")

        ref.get().addOnSuccessListener {
            val actual = it.value.toString().toIntOrNull() ?: 0
            ref.setValue(actual + 1)
        }
    }

    // ----------------------------------------------------------
    // MARCAR COMO VENDIDO
    // ----------------------------------------------------------
    private fun marcarComoVendido() {

        val ref = FirebaseDatabase.getInstance()
            .getReference("Anuncios")
            .child(idAnuncio)

        ref.child("estado").get().addOnSuccessListener { snapshot ->

            val estadoActual = snapshot.value.toString()

            val nuevoEstado = if (estadoActual == "Vendido") {
                "Disponible"
            } else {
                "Vendido"
            }

            ref.child("estado").setValue(nuevoEstado)
                .addOnSuccessListener {

                    binding.tvEstado.text = nuevoEstado

                    if (nuevoEstado == "Vendido") {
                        Toast.makeText(this, "Marcado como vendido", Toast.LENGTH_SHORT).show()
                        binding.btnVendido.text = "Marcar como Disponible"
                    } else {
                        Toast.makeText(this, "Marcado como disponible", Toast.LENGTH_SHORT).show()
                        binding.btnVendido.text = "Marcar como Vendido"
                    }
                }
        }
    }


    // ----------------------------------------------------------
    // ELIMINAR ANUNCIO
    // ----------------------------------------------------------
    private fun eliminarAnuncio() {
        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
            .child(idAnuncio)

        ref.removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Anuncio eliminado", Toast.LENGTH_SHORT).show()
                finish() // regresar automáticamente
            }
    }
}
