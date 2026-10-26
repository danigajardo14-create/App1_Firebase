package cl.inacap.app1_firebase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SeguridadTest {

    @Test
    fun rutValidoEsAceptado() {
        assertTrue(Seguridad.validarRut("11.111.111-1"))
        assertTrue(Seguridad.validarRut("22.222.222-2"))
        assertTrue(Seguridad.validarRut("12345678-5"))
        assertTrue(Seguridad.validarRut("10.000.013-K"))
        assertTrue(Seguridad.validarRut("10.000.013-k"))
    }

    @Test
    fun rutInvalidoEsRechazado() {
        assertFalse(Seguridad.validarRut("11.111.111-2"))
        assertFalse(Seguridad.validarRut(""))
        assertFalse(Seguridad.validarRut("abc"))
        assertFalse(Seguridad.validarRut("123456789-0"))
    }

    @Test
    fun rutSeNormalizaYFormatea() {
        assertEquals("11111111-1", Seguridad.normalizarRut("11.111.111-1"))
        assertEquals("11111111-1", Seguridad.normalizarRut(" 11111111-1 "))
        assertEquals("11.111.111-1", Seguridad.formatearRut("11111111-1"))
    }

    @Test
    fun hashTiene64CaracteresHexadecimales() {
        val hash = Seguridad.hashContrasena("11111111-1", "abc123")
        assertEquals(64, hash.length)
        assertTrue(hash.all { it in '0'..'9' || it in 'a'..'f' })
    }

    @Test
    fun hashNoContieneLaContrasena() {
        assertFalse(Seguridad.hashContrasena("11111111-1", "abc123").contains("abc123"))
    }

    @Test
    fun hashEsSiempreIgualParaLosMismosDatos() {
        assertEquals(
            Seguridad.hashContrasena("11.111.111-1", "abc123"),
            Seguridad.hashContrasena("11111111-1", "abc123")
        )
    }

    @Test
    fun hashCambiaConOtroUsuarioUOtraContrasena() {
        val base = Seguridad.hashContrasena("11111111-1", "abc123")
        assertNotEquals(base, Seguridad.hashContrasena("22222222-2", "abc123"))
        assertNotEquals(base, Seguridad.hashContrasena("11111111-1", "abc124"))
    }

    @Test
    fun contrasenaDebeTenerLetrasNumerosYMinimo6() {
        assertTrue(Seguridad.validarContrasena("abc123"))
        assertFalse(Seguridad.validarContrasena("abcdef"))
        assertFalse(Seguridad.validarContrasena("123456"))
        assertFalse(Seguridad.validarContrasena("ab12"))
    }
}
