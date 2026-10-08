package com.myciclo.app.calendario

import com.myciclo.app.registro.Nivel
import java.time.LocalDate

// un dia observado, sin nada de room para poder probarlo con junit
data class DiaObservado(val fecha: LocalDate, val nivel: Nivel)

data class Ventana(val inicio: LocalDate, val fin: LocalDate) {
    fun contiene(dia: LocalDate): Boolean = !dia.isBefore(inicio) && !dia.isAfter(fin)
}

// regla de la guia eva: la ventana parte con el primer helecho del ciclo
// y dura los 8 dias siguientes
// por ahora contamos alto y medio como helecho (lo podemos cambiar)
fun calcularVentana(dias: List<DiaObservado>, inicioCiclo: LocalDate): Ventana? {
    val conHelecho = dias.filter { !it.fecha.isBefore(inicioCiclo) && it.nivel != Nivel.NULO }
    if (conHelecho.isEmpty()) return null

    val primero = conHelecho.minOf { it.fecha }
    return Ventana(primero, primero.plusDays(8))
}
