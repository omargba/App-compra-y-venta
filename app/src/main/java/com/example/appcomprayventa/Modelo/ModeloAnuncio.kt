package com.example.appcomprayventa.Modelo

data class ModeloAnuncio(
    var id: String? = "",
    var uid: String? = "",
    var marca: String? = "",
    var categoria: String? = "",
    var condicion: String? = "",
    var direccion: String? = "",
    var precio: String? = "",
    var titulo: String? = "",
    var descripcion: String? = "",
    var estado: String? = "",
    var tiempo: String? = "",
    var latitud: Long? = 0,
    var longitud: Long? = 0,
    var contadorVisitas: Long? = 0,
    var primeraImagenUrl: String? = ""
)

