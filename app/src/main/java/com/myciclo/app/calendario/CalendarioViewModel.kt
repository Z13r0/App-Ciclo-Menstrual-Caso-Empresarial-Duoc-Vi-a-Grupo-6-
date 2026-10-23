package com.myciclo.app.calendario

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.myciclo.app.data.AppDatabase
import com.myciclo.app.registro.Nivel
import com.myciclo.app.registro.RegistroEntity
import com.myciclo.app.registro.RegistroRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

// lo que necesita la pantalla del calendario
// dias es solo el color de cada celda y detalles es el registro completo
// los separamos porque pintar el mes necesita poco y el dialogo necesita todo
data class CalendarioEstado(
    val dias: Map<LocalDate, Nivel> = emptyMap(),
    val detalles: Map<LocalDate, RegistroEntity> = emptyMap(),
    val ventana: Ventana? = null,
    val diaCiclo: Int? = null,
    val fase: Fase? = null
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
                // la ventana y el dia los calculamos con las funciones de este paquete
                // estan aparte para poder probarlas con junit sin levantar la app
                val hoy = LocalDate.now()
                val ventana = calcularVentana(dias, inicioCiclo)
                val dia = diaDelCiclo(inicioCiclo, hoy)
                CalendarioEstado(
                    dias = dias.associate { it.fecha to it.nivel },
                    // guardamos el registro entero con la fecha como llave
                    // asi el dialogo lo busca al instante sin volver a consultar room
                    detalles = lista.associateBy { LocalDate.parse(it.fecha) },
                    ventana = ventana,
                    diaCiclo = dia,
                    fase = faseDelCiclo(dia, ventana, hoy)
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, CalendarioEstado())
}
