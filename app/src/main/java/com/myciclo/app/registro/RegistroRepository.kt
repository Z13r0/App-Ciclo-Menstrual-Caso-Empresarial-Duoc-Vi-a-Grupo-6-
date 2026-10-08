package com.myciclo.app.registro

import kotlinx.coroutines.flow.Flow

// el repository solo usa room, el diario nunca se manda a internet
class RegistroRepository(private val dao: RegistroDao) {

    val registros: Flow<List<RegistroEntity>> = dao.todos()

    suspend fun guardar(registro: RegistroEntity) = dao.guardar(registro)

    suspend fun borrar(fecha: String) = dao.borrar(fecha)
}
