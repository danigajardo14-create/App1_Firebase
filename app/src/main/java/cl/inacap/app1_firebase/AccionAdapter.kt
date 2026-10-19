package cl.inacap.app1_firebase

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class AccionAdapter(context: Context, private val lista: List<Accion>) :
    ArrayAdapter<Accion>(context, 0, lista) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val vista = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_accion, parent, false)
        val a = lista[position]

        vista.findViewById<TextView>(R.id.tvitemfecha).text = ": ${a.fecha}"
        vista.findViewById<TextView>(R.id.tvitemhora).text = ": ${a.hora}"
        vista.findViewById<TextView>(R.id.tvitemusuario).text = ": ${Seguridad.formatearRut(a.usuario)}"
        val tvAccion = vista.findViewById<TextView>(R.id.tvitemaccion)
        tvAccion.text = ": ${a.accion}"
        tvAccion.setTextColor(if (a.accion == Accion.CERCA) Color.RED else Color.BLACK)
        vista.findViewById<TextView>(R.id.tvitemdistancia).text =
            if (a.distancia >= 0) ": ${a.distancia} cm" else ": -"
        return vista
    }
}
