package com.example.appcomprayventa.Fragmentos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appcomprayventa.Adaptadores.AdaptadorMisAnuncios
import com.example.appcomprayventa.Modelo.ModeloAnuncio
import com.example.appcomprayventa.databinding.FragmentMisAnunciosBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FragmentMisAnuncios : Fragment() {

    private lateinit var binding: FragmentMisAnunciosBinding
    private lateinit var adaptador: AdaptadorMisAnuncios
    private lateinit var firebaseAuth: FirebaseAuth

    private val listaAnuncios = ArrayList<ModeloAnuncio>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMisAnunciosBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.rvMisAnuncios.layoutManager = LinearLayoutManager(requireContext())
        adaptador = AdaptadorMisAnuncios(requireContext(), listaAnuncios)
        binding.rvMisAnuncios.adapter = adaptador


        cargarMisAnuncios()
        return binding.root
    }

    private fun cargarMisAnuncios() {

        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")

        ref.orderByChild("uid").equalTo(firebaseAuth.uid)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    listaAnuncios.clear()

                    for (ds in snapshot.children) {
                        val anuncio = ds.getValue(ModeloAnuncio::class.java)
                        // Cargar primera imagen manualmente
                        val nodosImagenes = ds.child("Imagenes")
                        for (img in nodosImagenes.children) {
                            anuncio?.primeraImagenUrl =
                                img.child("imagenUrl").value.toString()
                            break
                        }

                        if (anuncio != null) listaAnuncios.add(anuncio)
                    }

                    listaAnuncios.sortByDescending { it.tiempo?.toLongOrNull() }

                    adaptador.notifyDataSetChanged()

                    binding.tvVacio.visibility =
                        if (listaAnuncios.isEmpty()) View.VISIBLE else View.GONE
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}
