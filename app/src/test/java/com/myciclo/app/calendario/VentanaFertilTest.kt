package com.myciclo.app.calendario

import com.myciclo.app.registro.Nivel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class VentanaFertilTest {

    private val inicio = LocalDate.of(2026, 9, 1)

    @Test
    fun sinHelechoNoHayVentana() {
        val dias = listOf(DiaObservado(LocalDate.of(2026, 9, 5), Nivel.NULO))
        assertNull(calcularVentana(dias, inicio))
    }

    @Test
    fun ventanaEmpiezaEnElPrimerHelechoYDura8Dias() {
        val dias = listOf(
            DiaObservado(LocalDate.of(2026, 9, 16), Nivel.ALTO),
            DiaObservado(LocalDate.of(2026, 9, 12), Nivel.MEDIO),
            DiaObservado(LocalDate.of(2026, 9, 10), Nivel.NULO)
        )
        val ventana = calcularVentana(dias, inicio)!!
        assertEquals(LocalDate.of(2026, 9, 12), ventana.inicio)
        assertEquals(LocalDate.of(2026, 9, 20), ventana.fin)
        assertTrue(ventana.contiene(LocalDate.of(2026, 9, 20)))
        assertFalse(ventana.contiene(LocalDate.of(2026, 9, 21)))
    }

    @Test
    fun noCuentaHelechosDelCicloAnterior() {
        val dias = listOf(DiaObservado(LocalDate.of(2026, 8, 20), Nivel.ALTO))
        assertNull(calcularVentana(dias, inicio))
    }
}
