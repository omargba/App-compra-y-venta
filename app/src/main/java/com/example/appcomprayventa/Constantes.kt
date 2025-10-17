package com.example.appcomprayventa
import java.text.DateFormat
import java.util.Calendar
import java.util.Locale
import java.text.SimpleDateFormat


object Constantes {
    fun obtenerTiempoDis() : Long{
        return System.currentTimeMillis()
        }

    fun obtenerTiempo(tiempo: Long) : String{
        val calendario = Calendar.getInstance(Locale.ENGLISH)

        calendario.timeInMillis = tiempo

        return SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(calendario.time)

    }
}