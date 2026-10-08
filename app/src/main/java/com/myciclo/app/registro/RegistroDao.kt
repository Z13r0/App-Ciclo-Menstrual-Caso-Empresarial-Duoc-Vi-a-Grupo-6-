package com.myciclo.app.registro

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroDao {

    // devuelve un flow, cuando se guarda algo la lista se actualiza sola
    @Query("SELECT * FROM registros ORDER BY fecha DESC")
    fun todos(): Flow<List<RegistroEntity>>

    // si ya habia registro ese dia lo reemplaza
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(registro: RegistroEntity)

    @Query("DELETE FROM registros WHERE fecha = :fecha")
    suspend fun borrar(fecha: String)
}
