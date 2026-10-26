package cl.inacap.app1_firebase

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Estadisticas : AppCompatActivity() {

    private lateinit var tvusuest: TextView
    private lateinit var tvact: TextView
    private lateinit var tvdes: TextView
    private lateinit var tvobs: TextView
    private lateinit var tvpel: TextView
    private lateinit var tvdis: TextView
    private lateinit var btnvolest: Button

    private var usu: Usuario? = null

    private lateinit var refAcciones: DatabaseReference
    private var listener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_estadisticas)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvusuest = findViewById(R.id.tvusuest)
        tvact = findViewById(R.id.tvact)
        tvdes = findViewById(R.id.tvdes)
        tvobs = findViewById(R.id.tvobs)
        tvpel = findViewById(R.id.tvpel)
        tvdis = findViewById(R.id.tvdis)
        btnvolest = findViewById(R.id.btnvolest)

        usu = intent.getSerializableExtra("usu", Usuario::class.java)
        tvusuest.text = usu?.nombreCompleto()

        refAcciones = FirebaseDatabase.getInstance().reference.child(Accion.NODO_ACCIONES)

        calcularEstadisticas()
        volver()
        desactivarFlecha()
    }

    private fun calcularEstadisticas() {
        listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var activaciones = 0
                var desactivaciones = 0
                var obstaculos = 0
                var peligros = 0
                var distanciaTotal = 0

                for (hijo in snapshot.children) {
                    val a = hijo.getValue(Accion::class.java) ?: continue
                    when (a.accion) {
                        Accion.ACTIVADO -> activaciones++
                        Accion.DESACTIVADO -> desactivaciones++
                        Accion.OBSTACULO, Accion.CERCA -> {
                            obstaculos++
                            if (a.accion == Accion.CERCA) peligros++
                            if (a.distancia > 0) distanciaTotal += a.distancia
                        }
                    }
                }

                tvact.text = activaciones.toString()
                tvdes.text = desactivaciones.toString()
                tvobs.text = obstaculos.toString()
                tvpel.text = peligros.toString()
                tvdis.text = "$distanciaTotal cm"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Estadisticas, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
        refAcciones.addValueEventListener(listener!!)
    }

    private fun volver() {
        btnvolest.setOnClickListener {
            finish()
        }
    }

    private fun desactivarFlecha() {
        onBackPressedDispatcher.addCallback(this) {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        listener?.let { refAcciones.removeEventListener(it) }
    }
}
