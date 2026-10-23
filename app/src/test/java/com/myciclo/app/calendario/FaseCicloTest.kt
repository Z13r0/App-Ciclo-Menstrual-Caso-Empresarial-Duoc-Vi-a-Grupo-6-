package com.myciclo.app.calendario

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class FaseCicloTest {

    private val inicio = LocalDate.of(2026, 10, 1)

    @Test
    fun elPrimerDiaDeSangradoEsElDiaUno() {
        // mismo dia que el inicio del periodo, tiene que dar 1 y no 0
        assertEquals(1, diaDelCiclo(inicio, LocalDate.of(2026, 10, 1)))
    }

    @Test
    fun cuentaBienLosDiasDespues() {
        assertEquals(12, diaDelCiclo(inicio, LocalDate.of(2026, 10, 12)))
    }

    @Test
    fun losPrimerosCincoDiasSonMenstruacion() {
        assertEquals(Fase.MENSTRUACION, faseDelCiclo(1, null, LocalDate.of(2026, 10, 1)))
        assertEquals(Fase.MENSTRUACION, faseDelCiclo(5, null, LocalDate.of(2026, 10, 5)))
    }

    @Test
    fun delSeisAlTreceEsFolicular() {
        assertEquals(Fase.FOLICULAR, faseDelCiclo(6, null, LocalDate.of(2026, 10, 6)))
        assertEquals(Fase.FOLICULAR, faseDelCiclo(13, null, LocalDate.of(2026, 10, 13)))
    }

    @Test
    fun despuesDelTreceEsLutea() {
        assertEquals(Fase.LUTEA, faseDelCiclo(14, null, LocalDate.of(2026, 10, 14)))
    }

    @Test
    fun siElDiaCaeEnLaVentanaManda() {
        // el dia 20 seria fase lutea por calendario,
        // pero si la usuaria vio helecho y el dia esta en la ventana, mostramos la ventana
        val ventana = Ventana(LocalDate.of(2026, 10, 18), LocalDate.of(2026, 10, 26))
        assertEquals(Fase.VENTANA, faseDelCiclo(20, ventana, LocalDate.of(2026, 10, 20)))
    }

    @Test
    fun fueraDeLaVentanaVuelveAlCalendario() {
        val ventana = Ventana(LocalDate.of(2026, 10, 18), LocalDate.of(2026, 10, 26))
        assertEquals(Fase.LUTEA, faseDelCiclo(27, ventana, LocalDate.of(2026, 10, 27)))
    }
}
