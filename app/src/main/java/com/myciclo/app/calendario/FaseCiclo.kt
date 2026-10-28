package com.myciclo.app.calendario

import java.time.LocalDate
import java.time.temporal.ChronoUnit

enum class Fase(val texto: String) {
    MENSTRUACION("Menstruación"),
    FOLICULAR("Fase folicular"),
    VENTANA("Ventana EVA"),
    LUTEA("Fase lútea")
}

fun diaDelCiclo(inicioPeriodo: LocalDate, hoy: LocalDate): Int {
    return ChronoUnit.DAYS.between(inicioPeriodo, hoy).toInt() + 1
}

fun faseDelCiclo(dia: Int, ventana: Ventana?, hoy: LocalDate): Fase {
    if (ventana != null && ventana.contiene(hoy)) return Fase.VENTANA
    return when {
        dia <= 5 -> Fase.MENSTRUACION
        dia <= 13 -> Fase.FOLICULAR
        else -> Fase.LUTEA
    }
}