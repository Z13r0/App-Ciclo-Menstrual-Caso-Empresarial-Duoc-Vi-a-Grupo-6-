package com.myciclo.app.calendario

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.myciclo.app.data.AppDatabase
import com.myciclo.app.registro.Nivel
import com.myciclo.app.registro.RegistroEntity
import com.myciclo.app.registro.RegistroRepository
import com.myciclo.app.util.FechaUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs
import kotlin.math.roundToInt

data class EstadisticasCiclo(
    val promedio: Int,
    val variacion: Int
)

data class CalendarioEstado(
    val dias: Map<LocalDate, Nivel> = emptyMap(),
    val detalles: Map<LocalDate, RegistroEntity> = emptyMap(),
    val ventana: Ventana? = null,
    val diaCiclo: Int? = null,
    val fase: Fase? = null,
    val promedioCiclo: Int? = null,
    val variacionCiclo: Int? = null
)

class CalendarioViewModel @JvmOverloads constructor(
    app: Application,
    userId: String = ""
) : AndroidViewModel(app) {

    private val repository = RegistroRepository(
        AppDatabase.obtener(app).registroDao(),
        userId
    )

    val estado: StateFlow<CalendarioEstado> = repository.registros
        .map { lista ->
            if (lista.isEmpty()) {
                CalendarioEstado()
            } else {
                val dias = lista.mapNotNull {
                    val fechaParsed = FechaUtils.parsear(it.fecha) ?: return@mapNotNull null
                    val nivelParsed = try {
                        Nivel.valueOf(it.nivel)
                    } catch (_: Exception) {
                        Nivel.NULO
                    }
                    DiaObservado(fechaParsed, nivelParsed)
                }

                val inicioCiclo = lista.firstNotNullOfOrNull {
                    FechaUtils.parsear(it.inicioPeriodo)
                } ?: LocalDate.now()

                val hoy = LocalDate.now()
                val ventana = calcularVentana(dias, inicioCiclo)
                val dia = diaDelCiclo(inicioCiclo, hoy)
                val estadisticas = calcularEstadisticasCiclo(lista)

                CalendarioEstado(
                    dias = dias.associateBy({ it.fecha }, { it.nivel }),
                    detalles = lista.mapNotNull { reg ->
                        FechaUtils.parsear(reg.fecha)?.let { it to reg }
                    }.toMap(),
                    ventana = ventana,
                    diaCiclo = dia,
                    fase = faseDelCiclo(dia, ventana, hoy),
                    promedioCiclo = estadisticas?.promedio,
                    variacionCiclo = estadisticas?.variacion
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, CalendarioEstado())

    private fun calcularEstadisticasCiclo(registros: List<RegistroEntity>): EstadisticasCiclo? {
        val inicios = registros
            .map { it.inicioPeriodo }
            .filter { it.isNotBlank() }
            .distinct()
            .mapNotNull { FechaUtils.parsear(it) }
            .sorted()

        if (inicios.size < 2) return null

        val duraciones = inicios.zipWithNext { anterior, siguiente ->
            ChronoUnit.DAYS.between(anterior, siguiente).toInt()
        }.filter { it in 21..45 }

        if (duraciones.isEmpty()) return null

        val promedio = duraciones.average().roundToInt()
        val variacion = duraciones.maxOf { abs(it - promedio) }

        return EstadisticasCiclo(promedio, variacion)
    }
}
