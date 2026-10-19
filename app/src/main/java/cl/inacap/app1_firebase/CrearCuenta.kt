package cl.inacap.app1_firebase

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
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

class CrearCuenta : AppCompatActivity() {

    private lateinit var txtrutcre: EditText
    private lateinit var txtnomcre: EditText
    private lateinit var txtapecre: EditText
    private lateinit var txtcorcre: EditText
    private lateinit var spcargo: Spinner
    private lateinit var txtconcre: EditText
    private lateinit var txtrepcre: EditText
    private lateinit var btnreg: Button
    private lateinit var btnvolcre: Button

    private lateinit var db: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_cuenta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        txtrutcre = findViewById(R.id.txtrutcre)
        txtnomcre = findViewById(R.id.txtnomcre)
        txtapecre = findViewById(R.id.txtapecre)
        txtcorcre = findViewById(R.id.txtcorcre)
        spcargo = findViewById(R.id.spcargo)
        txtconcre = findViewById(R.id.txtconcre)
        txtrepcre = findViewById(R.id.txtrepcre)
        btnreg = findViewById(R.id.btnreg)
        btnvolcre = findViewById(R.id.btnvolcre)

        db = FirebaseDatabase.getInstance().reference

        registrar()
        volver()
        desactivarFlecha()
    }

    private fun registrar() {
        btnreg.setOnClickListener {
            val rut = txtrutcre.text.toString().trim()
            val nom = txtnomcre.text.toString().trim()
            val ape = txtapecre.text.toString().trim()
            val cor = txtcorcre.text.toString().trim().lowercase()
            val car = spcargo.selectedItem.toString()
            val con = txtconcre.text.toString()
            val rep = txtrepcre.text.toString()

            if (rut.isEmpty() || nom.isEmpty() || ape.isEmpty() || cor.isEmpty() || con.isEmpty() || rep.isEmpty()) {
                mensaje("Complete los campos faltantes")
            } else if (!Seguridad.validarRut(rut)) {
                mensaje("El RUT ingresado no es válido")
            } else if (!Seguridad.validarCorreo(cor)) {
                mensaje("El correo electrónico no es válido")
            } else if (spcargo.selectedItemPosition == 0) {
                mensaje("Seleccione un cargo")
            } else if (!Seguridad.validarContrasena(con)) {
                mensaje("La contraseña debe tener mínimo 6 caracteres, letras y números")
            } else if (con != rep) {
                txtrepcre.setText("")
                mensaje("Las contraseñas no coinciden")
            } else {
                btnreg.isEnabled = false
                val rutNorm = Seguridad.normalizarRut(rut)
                val usu = Usuario(
                    rut = rutNorm,
                    nombre = nom,
                    apellido = ape,
                    correo = cor,
                    cargo = car,
                    contrasena = Seguridad.hashContrasena(rutNorm, con)
                )
                asegurarSesion { guardarUsuario(usu) }
            }
        }
    }

    private fun asegurarSesion(alTerminar: () -> Unit) {
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            alTerminar()
        } else {
            auth.signInAnonymously()
                .addOnSuccessListener { alTerminar() }
                .addOnFailureListener {
                    btnreg.isEnabled = true
                    mensaje("No se pudo conectar con Firebase: ${it.message}")
                }
        }
    }

    private fun guardarUsuario(usu: Usuario) {
        val ref = db.child(Accion.NODO_USUARIOS).child(usu.rut)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    btnreg.isEnabled = true
                    mensaje("Ya existe una cuenta con ese RUT")
                } else {
                    ref.setValue(usu)
                        .addOnSuccessListener {
                            Toast.makeText(this@CrearCuenta, "Cuenta creada correctamente", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener {
                            btnreg.isEnabled = true
                            mensaje("Error al registrar: ${it.message}")
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                btnreg.isEnabled = true
                mensaje("Error de base de datos: ${error.message}")
            }
        })
    }

    private fun mensaje(texto: String) {
        Toast.makeText(this@CrearCuenta, texto, Toast.LENGTH_SHORT).show()
    }

    private fun volver() {
        btnvolcre.setOnClickListener {
            finish()
        }
    }

    private fun desactivarFlecha() {
        onBackPressedDispatcher.addCallback(this) {

        }
    }
}
