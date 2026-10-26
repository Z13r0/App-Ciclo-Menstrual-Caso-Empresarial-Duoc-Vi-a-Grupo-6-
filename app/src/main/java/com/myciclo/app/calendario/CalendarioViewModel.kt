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

/**
 * Estado que necesita la pantalla del calendario.
 * - dias: color de cada celda
 * - detalles: registro completo para el diálogo
 * - ventana: ventana fértil calculada con EVA
 * - diaCiclo / fase: información del día de hoy
 * - promedioCiclo: duración promedio en días (null si no hay suficientes datos)
 */
data class CalendarioEstado(
    val dias: Map<LocalDate, Nivel> = emptyMap(),
    val detalles: Map<LocalDate, RegistroEntity> = emptyMap(),
    val ventana: Ventana? = null,
    val diaCiclo: Int? = null,
    val fase: Fase? = null,
    val promedioCiclo: Int? = null
)

class CalendarioViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = RegistroRepository(AppDatabase.obtener(app).registroDao())

    // Cada vez que cambia la tabla se recalcula todo
    val estado: StateFlow<CalendarioEstado> = repository.registros
        .map { lista ->
            if (lista.isEmpty()) {
                CalendarioEstado()
            } else {
                val dias = lista.mapNotNull {
                    val fechaParsed = FechaUtils.parsear(it.fecha) ?: return@mapNotNull null
                    DiaObservado(fechaParsed, Nivel.valueOf(it.nivel))
                }
                // El ciclo actual lo marca el registro más nuevo (la lista viene ordenada DESC)
                val inicioCiclo = lista.firstNotNullOfOrNull { FechaUtils.parsear(it.inicioPeriodo) } ?: LocalDate.now()
                val hoy = LocalDate.now()
                val ventana = calcularVentana(dias, inicioCiclo)
                val dia = diaDelCiclo(inicioCiclo, hoy)

                CalendarioEstado(
                    dias = dias.associateBy({ it.fecha }, { it.nivel }),
                    detalles = lista.mapNotNull { reg ->
                        FechaUtils.parsear(reg.fecha)?.let { it to reg }
                    }.toMap(),
                    ventana = ventana,
                    diaCiclo = dia,
                    fase = faseDelCiclo(dia, ventana, hoy),
                    promedioCiclo = calcularPromedioCiclo(lista)
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, CalendarioEstado())

    /**
     * Calcula la duración promedio de los ciclos.
     * Toma los inicios de período distintos, los ordena y mide la diferencia entre uno y el siguiente.
     * Solo acepta duraciones entre 21 y 45 días (valores realistas).
     * Necesita al menos 2 inicios distintos; si no, devuelve null.
     */
    private fun calcularPromedioCiclo(registros: List<RegistroEntity>): Int? {
        val inicios = registros
            .map { it.inicioPeriodo }
            .distinct()
            .mapNotNull { FechaUtils.parsear(it) }
            .sorted()

        if (inicios.size < 2) return null

        val duraciones = inicios.zipWithNext { anterior, siguiente ->
            ChronoUnit.DAYS.between(anterior, siguiente).toInt()
        }.filter { (it in 21..45) }

        return if (duraciones.isEmpty()) null else duraciones.average().toInt()
    }
}
