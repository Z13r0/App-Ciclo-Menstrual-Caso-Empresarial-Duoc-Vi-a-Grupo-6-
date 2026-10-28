package com.myciclo.app.calendario

import com.myciclo.app.registro.Nivel
import java.time.LocalDate

data class DiaObservado(val fecha: LocalDate, val nivel: Nivel)

data class Ventana(val inicio: LocalDate, val fin: LocalDate) {
    fun contiene(dia: LocalDate): Boolean =
        !dia.isBefore(inicio) && !dia.isAfter(fin)
}

fun calcularVentana(dias: List<DiaObservado>, inicioCiclo: LocalDate): Ventana? {
    val conHelecho = dias.filter {
        !it.fecha.isBefore(inicioCiclo) && it.nivel != Nivel.NULO
    }
    if (conHelecho.isEmpty()) return null

    val primero = conHelecho.minOf { it.fecha }
    return Ventana(primero, primero.plusDays(8))
}