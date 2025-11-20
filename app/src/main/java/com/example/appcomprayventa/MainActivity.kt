package com.example.appcomprayventa

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.appcomprayventa.Anuncios.CrearAnuncio
import com.example.appcomprayventa.Fragmentos.FragmentChats
import com.example.appcomprayventa.Fragmentos.FragmentCuenta
import com.example.appcomprayventa.Fragmentos.FragmentInicio
import com.example.appcomprayventa.Fragmentos.FragmentMisAnuncios
import com.example.appcomprayventa.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseStorage: FirebaseStorage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()
        //firebaseAuth.signOut()
        comprobarSesion()

        VerFragmentInicio()

        binding.BottomNV.setOnItemSelectedListener { Item ->
            when (Item.itemId) {
                R.id.Item_Inicio->{
                    VerFragmentInicio()
                    true
                }
                R.id.Item_Chats->{
                    VerFragmentChats()
                    true
                }
                R.id.Item_Mis_Anuncios->{
                    VerFragmentMisAnuncios()
                    true
                }
                R.id.Item_Cuenta->{
                    VerFragmentCuenta()
                    true
                }
                else->{
                    false
                }
            }
        }

        binding.FAB.setOnClickListener {
            startActivity(Intent(this, CrearAnuncio::class.java))
        }
    }

    private fun comprobarSesion(){
        if(firebaseAuth.currentUser == null){
            startActivity(Intent(this, OpcionesLogin::class.java))
            finishAffinity()
        }
    }

    private fun VerFragmentInicio(){
        binding.TituloRL.setText("Inicio")
        val fragment = FragmentInicio()
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(binding.FragmentL1.id, fragment, "FragmentInicio").commit()
        //fragmentTransition.commit()
    }

    private fun VerFragmentChats(){
        binding.TituloRL.setText("Chats")
        val fragment = FragmentChats()
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(binding.FragmentL1.id, fragment, "FragmentChats").commit()
        //fragmentTransition.commit()
    }

    private fun VerFragmentMisAnuncios(){
        binding.TituloRL.setText("Mis Anuncios")
        val fragment = FragmentMisAnuncios()
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(binding.FragmentL1.id, fragment, "FragmentMisAnuncios").commit()
        //fragmentTransition.commit()
    }

    private fun VerFragmentCuenta(){
        binding.TituloRL.setText("Cuenta")
        val fragment = FragmentCuenta()
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(binding.FragmentL1.id, fragment, "FragmentCuenta").commit()
        //fragmentTransition.commit()
    }
}