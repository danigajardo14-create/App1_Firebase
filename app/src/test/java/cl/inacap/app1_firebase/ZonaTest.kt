package cl.inacap.app1_firebase

import org.junit.Assert.assertEquals
import org.junit.Test

class ZonaTest {

    @Test
    fun masDe100cmEsZonaSegura() {
        assertEquals(Accion.SEGURA, Accion.zona(150))
        assertEquals(Accion.SEGURA, Accion.zona(101))
    }

    @Test
    fun entre31y100cmEsPrecaucion() {
        assertEquals(Accion.OBSTACULO, Accion.zona(100))
        assertEquals(Accion.OBSTACULO, Accion.zona(31))
    }

    @Test
    fun hasta30cmEsPeligro() {
        assertEquals(Accion.CERCA, Accion.zona(30))
        assertEquals(Accion.CERCA, Accion.zona(5))
    }

    @Test
    fun sinLecturaNoGeneraAlerta() {
        assertEquals(Accion.SEGURA, Accion.zona(-1))
        assertEquals(Accion.SEGURA, Accion.zona(0))
    }
}
