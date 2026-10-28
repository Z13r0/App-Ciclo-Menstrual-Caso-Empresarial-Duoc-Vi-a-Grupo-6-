package com.myciclo.app.registro

import com.myciclo.app.util.FechaUtils
import kotlinx.coroutines.flow.Flow

class RegistroRepository(
    private val dao: RegistroDao,
    private val userId: String
) {
    val registros: Flow<List<RegistroEntity>> = dao.todos(userId)

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
        dao.borrar(userId, fechaIso)
    }

    suspend fun reclamarRegistrosHeredados() =
        dao.reclamarRegistrosHeredados(userId)
}