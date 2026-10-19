package cl.inacap.app1_firebase

import android.util.Patterns
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Seguridad {

    private const val PIMIENTA = "TI3042-IoT"

    fun hashContrasena(rut: String, contrasena: String): String {
        val texto = "${normalizarRut(rut)}:$contrasena:$PIMIENTA"
        val bytes = MessageDigest.getInstance("SHA-256").digest(texto.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun normalizarRut(rut: String): String {
        val limpio = rut.replace(".", "").replace("-", "").replace(" ", "").uppercase()
        if (limpio.length < 2) return limpio
        return limpio.dropLast(1) + "-" + limpio.last()
    }

    fun formatearRut(rut: String): String {
        val norm = normalizarRut(rut)
        val partes = norm.split("-")
        if (partes.size != 2) return rut
        val cuerpo = partes[0].reversed().chunked(3).joinToString(".").reversed()
        return "$cuerpo-${partes[1]}"
    }

    fun validarRut(rut: String): Boolean {
        val norm = normalizarRut(rut)
        val partes = norm.split("-")
        if (partes.size != 2) return false
        val cuerpo = partes[0]
        val dv = partes[1]
        if (cuerpo.isEmpty() || cuerpo.length > 8 || !cuerpo.all { it.isDigit() }) return false
        var suma = 0
        var multiplicador = 2
        for (c in cuerpo.reversed()) {
            suma += (c - '0') * multiplicador
            multiplicador = if (multiplicador == 7) 2 else multiplicador + 1
        }
        val resto = 11 - (suma % 11)
        val dvEsperado = when (resto) {
            11 -> "0"
            10 -> "K"
            else -> resto.toString()
        }
        return dv == dvEsperado
    }

    fun validarCorreo(correo: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(correo).matches()

    fun validarContrasena(contrasena: String): Boolean =
        contrasena.length >= 6 && contrasena.any { it.isLetter() } && contrasena.any { it.isDigit() }

    fun fechaActual(): String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
    fun horaActual(): String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
}
