package com.example.appcomprayventa.Anuncios

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.appcomprayventa.Adaptadores.AdaptadorSliderImagenes
import com.example.appcomprayventa.databinding.ActivityDetalleAnuncioBinding
import com.google.firebase.database.*

class DetalleAnuncio : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleAnuncioBinding
    private lateinit var idAnuncio: String
    private var estadoActual: String = "Disponible"


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

        binding.btnVendido.setOnClickListener {
            alternarEstado()
        }

    }

    // ----------------------------------------------------------
    // CARGAR DETALLES
    // ----------------------------------------------------------
    private fun cargarDetalle() {
        val ref = FirebaseDatabase.getInstance().getReference("Anuncios").child(idAnuncio)

        ref.get().addOnSuccessListener { snapshot ->

            //----------------------------------------------------------
            // 1️⃣ CARGAR TODAS LAS IMÁGENS PARA EL SLIDER
            //----------------------------------------------------------
            val listaImagenes = ArrayList<String>()
            val imagenesSnap = snapshot.child("Imagenes")

            for (img in imagenesSnap.children) {
                val url = img.child("imagenUrl").value.toString()
                if (url.isNotEmpty()) listaImagenes.add(url)
            }

            // Si no hay imágenes, agrega una de relleno
            if (listaImagenes.isEmpty()) {
                listaImagenes.add("https://via.placeholder.com/400")
            }

            // Asignar adaptador al ViewPager2
            val adaptadorSlider = AdaptadorSliderImagenes(this, listaImagenes)
            binding.viewPagerImagenes.adapter = adaptadorSlider

            // Conectar dots
            binding.dotsIndicator.attachTo(binding.viewPagerImagenes)

            //----------------------------------------------------------
            // 2️⃣ CARGAR TEXTO Y DEMÁS CAMPOS
            //----------------------------------------------------------
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

            //----------------------------------------------------------
            // 3️⃣ GUARDAR Y AJUSTAR ESTADO
            //----------------------------------------------------------
            estadoActual = estado

            binding.btnVendido.text =
                if (estado == "Vendido") "Marcar como Disponible"
                else "Marcar como Vendido"

            //----------------------------------------------------------
            // 4️⃣ VISITAS
            //----------------------------------------------------------
            val visitas = snapshot.child("contadorVistas").value?.toString() ?: "0"
            binding.tvVisitas.text = "👁 $visitas visitas"
        }
    }


    private fun alternarEstado() {
        val ref = FirebaseDatabase.getInstance().getReference("Anuncios").child(idAnuncio)

        val nuevoEstado =
            if (estadoActual == "Vendido") "Disponible"
            else "Vendido"

        ref.child("estado").setValue(nuevoEstado)
            .addOnSuccessListener {

                // Actualizar UI
                estadoActual = nuevoEstado
                binding.tvEstado.text = nuevoEstado

                if (nuevoEstado == "Vendido") {
                    binding.btnVendido.text = "Marcar como Disponible"
                } else {
                    binding.btnVendido.text = "Marcar como Vendido"
                }
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
