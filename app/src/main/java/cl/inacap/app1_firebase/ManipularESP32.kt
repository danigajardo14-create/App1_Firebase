package cl.inacap.app1_firebase

import android.graphics.Color
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
import kotlin.random.Random

class ManipularESP32 : AppCompatActivity() {

    private lateinit var tvusumanipular: TextView
    private lateinit var tvestado: TextView
    private lateinit var tvultimo: TextView
    private lateinit var btnon: Button
    private lateinit var btnoff: Button
    private lateinit var btnsensor: Button
    private lateinit var btnvolman: Button

    private var usu: Usuario? = null
    private var circuitoActivo = false

    private lateinit var refCircuito: DatabaseReference
    private lateinit var refAcciones: DatabaseReference
    private var listenerCircuito: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manipular_esp32)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvusumanipular = findViewById(R.id.tvusumanipular)
        tvestado = findViewById(R.id.tvestado)
        tvultimo = findViewById(R.id.tvultimo)
        btnon = findViewById(R.id.btnon)
        btnoff = findViewById(R.id.btnoff)
        btnsensor = findViewById(R.id.btnsensor)
        btnvolman = findViewById(R.id.btnvolman)

        usu = intent.getSerializableExtra("usu", Usuario::class.java)
        tvusumanipular.text = usu?.nombreCompleto()

        val db = FirebaseDatabase.getInstance().reference
        refCircuito = db.child(Accion.NODO_CIRCUITO)
        refAcciones = db.child(Accion.NODO_ACCIONES)

        escucharCircuito()
        activar()
        desactivar()
        simularSensor()
        volver()
        desactivarFlecha()
    }

    private fun escucharCircuito() {
        listenerCircuito = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                circuitoActivo = snapshot.child("estado").value as? Boolean ?: false
                val por = snapshot.child("usuario").getValue(String::class.java) ?: "-"
                val hora = snapshot.child("hora").getValue(String::class.java) ?: ""
                val ultimaAccion = snapshot.child("ultimaAccion").getValue(String::class.java) ?: ""
                val ultimaDist = (snapshot.child("ultimaDistancia").value as? Long)?.toInt() ?: -1

                if (circuitoActivo) {
                    tvestado.text = "Estado: ACTIVADO"
                    tvestado.setTextColor(getColor(R.color.verde_on))
                } else {
                    tvestado.text = "Estado: DESACTIVADO"
                    tvestado.setTextColor(getColor(R.color.peligro))
                }
                btnsensor.isEnabled = circuitoActivo

                var detalle = "Última acción: $ultimaAccion\nPor: ${Seguridad.formatearRut(por)}  $hora"
                if (ultimaDist >= 0) detalle += "\nDistancia: $ultimaDist cm"
                tvultimo.text = if (ultimaAccion.isEmpty()) "Sin acciones registradas" else detalle
                tvultimo.setTextColor(if (ultimaAccion == Accion.CERCA) Color.RED else Color.DKGRAY)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ManipularESP32, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
        refCircuito.addValueEventListener(listenerCircuito!!)
    }

    private fun activar() {
        btnon.setOnClickListener {
            if (circuitoActivo) {
                Toast.makeText(this@ManipularESP32, "El circuito ya está activado", Toast.LENGTH_SHORT).show()
            } else {
                registrarAccion(Accion.ACTIVADO, -1, true)
            }
        }
    }

    private fun desactivar() {
        btnoff.setOnClickListener {
            if (!circuitoActivo) {
                Toast.makeText(this@ManipularESP32, "El circuito ya está desactivado", Toast.LENGTH_SHORT).show()
            } else {
                registrarAccion(Accion.DESACTIVADO, -1, false)
            }
        }
    }

    private fun simularSensor() {
        btnsensor.setOnClickListener {
            val distancia = Random.nextInt(2, 61)
            val tipo = if (distancia <= Accion.DISTANCIA_PELIGRO) Accion.CERCA else Accion.OBSTACULO
            registrarAccion(tipo, distancia, true)
        }
    }

    private fun registrarAccion(tipo: String, distancia: Int, estado: Boolean) {
        val accion = Accion(
            fecha = Seguridad.fechaActual(),
            hora = Seguridad.horaActual(),
            usuario = usu?.rut ?: "",
            accion = tipo,
            distancia = distancia,
            timestamp = System.currentTimeMillis()
        )
        refAcciones.push().setValue(accion)

        val datosCircuito = mapOf(
            "estado" to estado,
            "usuario" to accion.usuario,
            "fecha" to accion.fecha,
            "hora" to accion.hora,
            "ultimaAccion" to tipo,
            "ultimaDistancia" to distancia
        )
        refCircuito.setValue(datosCircuito)
            .addOnSuccessListener {
                Toast.makeText(this@ManipularESP32, tipo, Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this@ManipularESP32, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun volver() {
        btnvolman.setOnClickListener {
            finish()
        }
    }

    private fun desactivarFlecha() {
        onBackPressedDispatcher.addCallback(this) {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerCircuito?.let { refCircuito.removeEventListener(it) }
    }
}
