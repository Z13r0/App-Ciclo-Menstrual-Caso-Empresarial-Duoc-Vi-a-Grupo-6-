package com.myciclo.app.calendario

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.myciclo.app.data.AppDatabase
import com.myciclo.app.registro.Nivel
import com.myciclo.app.registro.RegistroRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.temporal.ChronoUnit

// lo que necesita la pantalla del calendario
data class CalendarioEstado(
    val dias: Map<LocalDate, Nivel> = emptyMap(),
    val ventana: Ventana? = null,
    val diaCiclo: Int? = null
)

class CalendarioViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = RegistroRepository(AppDatabase.obtener(app).registroDao())

    // cada vez que cambia la tabla se recalcula todo
    val estado: StateFlow<CalendarioEstado> = repository.registros
        .map { lista ->
            if (lista.isEmpty()) {
                CalendarioEstado()
            } else {
                val dias = lista.map { DiaObservado(LocalDate.parse(it.fecha), Nivel.valueOf(it.nivel)) }
                // el ciclo actual lo marca el registro mas nuevo (la lista viene ordenada)
                val inicioCiclo = LocalDate.parse(lista.first().inicioPeriodo)
                CalendarioEstado(
                    dias = dias.associate { it.fecha to it.nivel },
                    ventana = calcularVentana(dias, inicioCiclo),
                    diaCiclo = ChronoUnit.DAYS.between(inicioCiclo, LocalDate.now()).toInt() + 1
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, CalendarioEstado())
}
