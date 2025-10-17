package com.example.appcomprayventa

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.appcomprayventa.Opciones_Login.Login_email
import com.example.appcomprayventa.databinding.ActivityOpcionesLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase


class OpcionesLogin : AppCompatActivity() {

    private lateinit var binding: ActivityOpcionesLoginBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpcionesLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar ProgressDialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Por favor espere")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()
        comprobarSesion()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.IngresarGoogle.setOnClickListener {
            googleLogin()
        }




        binding.IngresarEmail.setOnClickListener {
            startActivity(Intent(this@OpcionesLogin, Login_email::class.java))
        }
    }

    private fun googleLogin() {
        val googleSignInIntent = mGoogleSignInClient.signInIntent
        googleSignInArl.launch(googleSignInIntent)
    }

    private fun comprobarSesion() {
        if (firebaseAuth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }
    }

    // ... (código anterior)

    private fun autenticacionGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { resultadoAuth ->
                resultadoAuth.additionalUserInfo?.let { info ->
                    if (info.isNewUser) {

                        llenarInfoBD()
                    } else {
                        startActivity(Intent(this, MainActivity::class.java))
                        finishAffinity()
                    }
                } ?: run {
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private val googleSignInArl = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            resultado ->
        if(resultado.resultCode == RESULT_OK){
            val data = resultado.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val cuenta = task.getResult(ApiException::class.java)
                autenticacionGoogle(cuenta.idToken)
            }catch (e: Exception){
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun llenarInfoBD(){
        progressDialog.setMessage("Guardando información")
        progressDialog.show()

        val user = firebaseAuth.currentUser

        if (user == null) {
            progressDialog.dismiss()
            Toast.makeText(this, "Error, no se pudo obtener la información del usuario.", Toast.LENGTH_SHORT).show()
            return
        }

        val tiempo = Constantes.obtenerTiempoDis()
        val emailUsuario = user.email
        val uidUsuario = user.uid
        val nombreUsuario = user.displayName

        val hashMap = HashMap<String, Any>()
        hashMap["nombres"] = nombreUsuario ?: ""
        hashMap["codigoTelefono"] = ""
        hashMap["telefono"] = ""
        hashMap["urlImagenPerfil"] = ""
        hashMap["proveedor"] = "Google"
        hashMap["escribiendo"] = ""
        hashMap["tiempo"] = tiempo
        hashMap["online"] = true
        hashMap["email"] = emailUsuario ?: ""
        hashMap["uid"] = uidUsuario
        hashMap["fecha_nac"] = ""


        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(uidUsuario)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
            .addOnFailureListener{
                progressDialog.dismiss()
                Toast.makeText(this,
                    "No se registró el usuario debido a ${it.message}",
                    Toast.LENGTH_SHORT).show()
            }
    }
}