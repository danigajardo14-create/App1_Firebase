package cl.inacap.app1_firebase

data class Accion(
    var fecha: String = "",
    var hora: String = "",
    var usuario: String = "",
    var accion: String = "",
    var distancia: Int = -1,
    var timestamp: Long = 0
) {
    companion object {
        const val NODO_USUARIOS = "usuarios"
        const val NODO_ACCIONES = "acciones"
        const val NODO_CIRCUITO = "circuito"

        const val ACTIVADO = "Circuito Activado"
        const val DESACTIVADO = "Circuito Desactivado"
        const val SEGURA = "Zona Segura"
        const val OBSTACULO = "Obstáculo Detectado"
        const val CERCA = "Cerca de Impactar"

        const val DISTANCIA_PRECAUCION = 100
        const val DISTANCIA_PELIGRO = 30

        fun zona(distancia: Int): String = when {
            distancia > DISTANCIA_PRECAUCION -> SEGURA
            distancia > DISTANCIA_PELIGRO -> OBSTACULO
            distancia > 0 -> CERCA
            else -> SEGURA
        }
    }
}
