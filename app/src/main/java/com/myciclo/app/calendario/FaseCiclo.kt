package com.myciclo.app.calendario

import java.time.LocalDate
import java.time.temporal.ChronoUnit

// fases que nombra el material educativo de myciclo
// el texto es lo que se muestra en pantalla, el nombre del enum lo usamos en el codigo
enum class Fase(val texto: String) {
    MENSTRUACION("Menstruación"),
    FOLICULAR("Fase folicular"),
    VENTANA("Ventana EVA"),
    LUTEA("Fase lútea")
}

// el primer dia de sangrado es el dia 1 del ciclo, no el dia 0
// por eso sumamos 1 a la diferencia de dias
fun diaDelCiclo(inicioPeriodo: LocalDate, hoy: LocalDate): Int {
    return ChronoUnit.DAYS.between(inicioPeriodo, hoy).toInt() + 1
}

// fase aproximada del ciclo
// si el dia cae dentro de la ventana que calculamos con eva, esa manda:
// el helecho es lo que la usuaria observo de verdad y no una estimacion por calendario
// los cortes 5 y 13 son los del material, igual los ciclos no duran lo mismo en todas
fun faseDelCiclo(dia: Int, ventana: Ventana?, hoy: LocalDate): Fase {
    if (ventana != null && ventana.contiene(hoy)) return Fase.VENTANA
    return when {
        dia <= 5 -> Fase.MENSTRUACION
        dia <= 13 -> Fase.FOLICULAR
        else -> Fase.LUTEA
    }
}
