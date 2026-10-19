package cl.inacap.app1_firebase

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Perfil : AppCompatActivity() {

    private lateinit var tvusuperfil: TextView
    private lateinit var tvrutper: TextView
    private lateinit var tvnomper: TextView
    private lateinit var tvapeper: TextView
    private lateinit var tvcorper: TextView
    private lateinit var tvcarper: TextView
    private lateinit var tvconper: TextView
    private lateinit var btnvolper: Button

    private var usu: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvusuperfil = findViewById(R.id.tvusuperfil)
        tvrutper = findViewById(R.id.tvrutper)
        tvnomper = findViewById(R.id.tvnomper)
        tvapeper = findViewById(R.id.tvapeper)
        tvcorper = findViewById(R.id.tvcorper)
        tvcarper = findViewById(R.id.tvcarper)
        tvconper = findViewById(R.id.tvconper)
        btnvolper = findViewById(R.id.btnvolper)

        usu = intent.getSerializableExtra("usu", Usuario::class.java)
        tvusuperfil.text = usu?.nombreCompleto()

        tvrutper.text = Seguridad.formatearRut(usu?.rut ?: "")
        tvnomper.text = usu?.nombre
        tvapeper.text = usu?.apellido
        tvcorper.text = usu?.correo
        tvcarper.text = usu?.cargo
        tvconper.text = "********"

        volver()
        desactivarFlecha()
    }

    private fun volver() {
        btnvolper.setOnClickListener {
            finish()
        }
    }

    private fun desactivarFlecha() {
        onBackPressedDispatcher.addCallback(this) {

        }
    }
}
