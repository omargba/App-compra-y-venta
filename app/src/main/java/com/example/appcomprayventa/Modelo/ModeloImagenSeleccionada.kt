package com.example.appcomprayventa.Modelo

import android.net.Uri

class ModeloImagenSeleccionada {
    var id = ""
    var imagenUri : Uri?= null
    var imagenUri2 : String?= null
    var deInternet = false

    constructor()
    constructor(id: String, imagenUri: Uri?, imagenUri2: String?, deInternet: Boolean) {
        this.id = id
        this.imagenUri = imagenUri
        this.imagenUri2 = imagenUri2
        this.deInternet = deInternet
    }


}