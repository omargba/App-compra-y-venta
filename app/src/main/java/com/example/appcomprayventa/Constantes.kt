package com.example.appcomprayventa
import java.text.DateFormat
import java.util.Calendar
import java.util.Locale
import java.text.SimpleDateFormat


object Constantes {
    const val anuncio_disponible = "Disponible"
    const val anuncio_vendido = "Vendido"

    val categorias = arrayOf(
        "Celulares",
        "PCs/Laptops",
        "Electronica y electrodomesticos",
        "Automoviles",
        "Consolas y videojuegos",
        "Hogar y muebles",
        "Belleza y cuidado personal",
        "Libros",
        "Deportes"
    )

    val condiciones = arrayOf(
        "Nuevo",
        "Usado",
        "Reacondicionado"
    )


    fun obtenerTiempoDis() : Long{
        return System.currentTimeMillis()
        }

    fun obtenerTiempo(tiempo: Long) : String{
        val calendario = Calendar.getInstance(Locale.ENGLISH)

        calendario.timeInMillis = tiempo

        return SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(calendario.time)

    }
}