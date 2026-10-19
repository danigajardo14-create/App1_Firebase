package cl.inacap.app1_firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Menu : AppCompatActivity() {

    private lateinit var tvusumenu: TextView
    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button
    private lateinit var btn4: Button
    private lateinit var btn5: Button

    private var usu: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvusumenu = findViewById(R.id.tvusumenu)
        btn1 = findViewById(R.id.btn1)
        btn2 = findViewById(R.id.btn2)
        btn3 = findViewById(R.id.btn3)
        btn4 = findViewById(R.id.btn4)
        btn5 = findViewById(R.id.btn5)

        usu = intent.getSerializableExtra("usu", Usuario::class.java)
        tvusumenu.text = usu?.nombreCompleto()

        boton1()
        boton2()
        boton3()
        boton4()
        boton5()
        desactivarFlecha()
    }

    private fun boton1() {
        btn1.setOnClickListener { abrir(ManipularESP32::class.java) }
    }

    private fun boton2() {
        btn2.setOnClickListener { abrir(Acciones::class.java) }
    }

    private fun boton3() {
        btn3.setOnClickListener { abrir(Estadisticas::class.java) }
    }

    private fun boton4() {
        btn4.setOnClickListener { abrir(Perfil::class.java) }
    }

    private fun abrir(destino: Class<*>) {
        val intent = Intent(this@Menu, destino)
        intent.putExtra("usu", usu)
        startActivity(intent)
    }

    private fun boton5() {
        btn5.setOnClickListener {
            val a = AlertDialog.Builder(this@Menu)
            a.setTitle("Pregunta")
            a.setMessage("¿Desea cerrar sesión?")
            a.setIcon(R.drawable.pregunta)
            a.setCancelable(false)
            a.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            a.setPositiveButton("Aceptar") { _, _ ->
                usu = null
                val intent = Intent(this@Menu, Principal::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            a.show()
        }
    }

    private fun desactivarFlecha() {
        onBackPressedDispatcher.addCallback(this) {

        }
    }
}
