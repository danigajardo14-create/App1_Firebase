package cl.inacap.app1_firebase

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Principal : AppCompatActivity() {

    private lateinit var txtrut: EditText
    private lateinit var txtcon: EditText
    private lateinit var btnini: Button
    private lateinit var btncrear: Button

    private lateinit var db: DatabaseReference

    private var intentosFallidos = 0
    private var bloqueadoHasta = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_principal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        txtrut = findViewById(R.id.txtrut)
        txtcon = findViewById(R.id.txtcon)
        btnini = findViewById(R.id.btnini)
        btncrear = findViewById(R.id.btncrear)

        db = FirebaseDatabase.getInstance().reference

        conectarFirebase()
        botonLogin()
        botonCrearCuenta()
        desactivarFlecha()
    }

    private fun conectarFirebase(alTerminar: (() -> Unit)? = null) {
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            alTerminar?.invoke()
            return
        }
        auth.signInAnonymously()
            .addOnSuccessListener { alTerminar?.invoke() }
            .addOnFailureListener {
                btnini.isEnabled = true
                Toast.makeText(this@Principal, "No se pudo conectar con Firebase: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun botonLogin() {
        btnini.setOnClickListener {
            val rut = txtrut.text.toString().trim()
            val con = txtcon.text.toString()

            if (System.currentTimeMillis() < bloqueadoHasta) {
                val seg = (bloqueadoHasta - System.currentTimeMillis()) / 1000 + 1
                Toast.makeText(this@Principal, "Demasiados intentos. Espere $seg segundos", Toast.LENGTH_SHORT).show()
            } else if (rut.isEmpty() || con.trim().isEmpty()) {
                Toast.makeText(this@Principal, "Complete los campos faltantes", Toast.LENGTH_SHORT).show()
            } else if (!Seguridad.validarRut(rut)) {
                Toast.makeText(this@Principal, "El RUT ingresado no es válido", Toast.LENGTH_SHORT).show()
            } else {
                ocultarTeclado()
                btnini.isEnabled = false
                conectarFirebase { validarUsuario(rut, con) }
            }
        }
    }

    private fun validarUsuario(rut: String, con: String) {
        val rutNorm = Seguridad.normalizarRut(rut)
        db.child(Accion.NODO_USUARIOS).child(rutNorm)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    btnini.isEnabled = true
                    val usu = snapshot.getValue(Usuario::class.java)
                    if (usu != null && usu.contrasena == Seguridad.hashContrasena(rutNorm, con)) {
                        intentosFallidos = 0
                        txtrut.setText("")
                        txtcon.setText("")
                        val intent = Intent(this@Principal, Menu::class.java)
                        intent.putExtra("usu", usu)
                        startActivity(intent)
                    } else {
                        loginFallido()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    btnini.isEnabled = true
                    Toast.makeText(this@Principal, "Error de base de datos: ${error.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun loginFallido() {
        intentosFallidos++
        txtcon.setText("")
        if (intentosFallidos >= 3) {
            intentosFallidos = 0
            bloqueadoHasta = System.currentTimeMillis() + 30_000
            Toast.makeText(this@Principal, "3 intentos fallidos: acceso bloqueado por 30 segundos", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this@Principal, "RUT o contraseña incorrectos (intento $intentosFallidos de 3)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun botonCrearCuenta() {
        btncrear.setOnClickListener {
            val intent = Intent(this@Principal, CrearCuenta::class.java)
            startActivity(intent)
        }
    }

    private fun desactivarFlecha() {
        onBackPressedDispatcher.addCallback(this) {

        }
    }

    private fun ocultarTeclado() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
