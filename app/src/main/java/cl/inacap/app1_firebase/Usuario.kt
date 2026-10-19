package cl.inacap.app1_firebase
import java.io.Serializable

data class Usuario(
    var rut: String = "",
    var nombre: String = "",
    var apellido: String = "",
    var correo: String = "",
    var cargo: String = "",
    var contrasena: String = ""
) : Serializable {
    fun nombreCompleto(): String = "$nombre $apellido".trim()
}
