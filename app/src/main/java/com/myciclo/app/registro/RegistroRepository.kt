package com.myciclo.app.registro

import com.myciclo.app.util.FechaUtils
import kotlinx.coroutines.flow.Flow

// El repository maneja los datos con Room, garantizando fechas en formato ISO estándar ("yyyy-MM-dd")
class RegistroRepository(private val dao: RegistroDao) {

    val registros: Flow<List<RegistroEntity>> = dao.todos()

    suspend fun guardar(registro: RegistroEntity) {
        val fechaIso = FechaUtils.parsear(registro.fecha)?.let { FechaUtils.aFormatoIso(it) } ?: registro.fecha
        val inicioIso = FechaUtils.parsear(registro.inicioPeriodo)?.let { FechaUtils.aFormatoIso(it) } ?: registro.inicioPeriodo

        dao.guardar(
            registro.copy(
                fecha = fechaIso,
                inicioPeriodo = inicioIso
            )
        )
    }

    suspend fun borrar(fecha: String) {
        val fechaIso = FechaUtils.parsear(fecha)?.let { FechaUtils.aFormatoIso(it) } ?: fecha
        dao.borrar(fechaIso)
    }
}
