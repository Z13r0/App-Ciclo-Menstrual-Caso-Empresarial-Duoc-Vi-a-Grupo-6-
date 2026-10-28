package com.myciclo.app.registro

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroDao {

    @Query("SELECT * FROM registros WHERE userId = :userId ORDER BY fecha DESC")
    fun todos(userId: String): Flow<List<RegistroEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(registro: RegistroEntity)

    @Query("DELETE FROM registros WHERE userId = :userId AND fecha = :fecha")
    suspend fun borrar(userId: String, fecha: String)

    @Query("UPDATE registros SET userId = :userId WHERE userId = :legacyUserId")
    suspend fun reclamarRegistrosHeredados(
        userId: String,
        legacyUserId: String = LEGACY_USER_ID
    )

    companion object {
        const val LEGACY_USER_ID = "legacy-local"
    }
}