package cl.inacap.app1_firebase

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class Acciones : AppCompatActivity() {

    private lateinit var tvusuacciones: TextView
    private lateinit var tvvacio: TextView
    private lateinit var lvacciones: ListView
    private lateinit var btnvolacc: Button

    private var usu: Usuario? = null

    private val lista = ArrayList<Accion>()
    private lateinit var adaptador: AccionAdapter
    private lateinit var consulta: Query
    private var listener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_acciones)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvusuacciones = findViewById(R.id.tvusuacciones)
        tvvacio = findViewById(R.id.tvvacio)
        lvacciones = findViewById(R.id.lvacciones)
        btnvolacc = findViewById(R.id.btnvolacc)

        usu = intent.getSerializableExtra("usu", Usuario::class.java)
        tvusuacciones.text = usu?.nombreCompleto()

        adaptador = AccionAdapter(this, lista)
        lvacciones.adapter = adaptador

        cargarAcciones()
        volver()
        desactivarFlecha()
    }

    private fun cargarAcciones() {
        consulta = FirebaseDatabase.getInstance().reference
            .child(Accion.NODO_ACCIONES)
            .orderByChild("timestamp")
            .limitToLast(50)

        listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lista.clear()
                for (hijo in snapshot.children) {
                    hijo.getValue(Accion::class.java)?.let { lista.add(it) }
                }
                lista.reverse()
                adaptador.notifyDataSetChanged()
                tvvacio.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Acciones, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
        consulta.addValueEventListener(listener!!)
    }

    private fun volver() {
        btnvolacc.setOnClickListener {
            finish()
        }
    }

    private fun desactivarFlecha() {
        onBackPressedDispatcher.addCallback(this) {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        listener?.let { consulta.removeEventListener(it) }
    }
}
