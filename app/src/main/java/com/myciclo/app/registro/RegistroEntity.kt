package com.myciclo.app.registro

import androidx.room.Entity
import androidx.room.PrimaryKey

// una fila por dia, la fecha es la llave asi no se repiten dias
// las fechas van como texto "2026-10-08" para que sea facil leerlas
// foto guarda la ruta del archivo dentro de la app, vacia si la usuaria no saco foto
@Entity(tableName = "registros")
data class RegistroEntity(
    @PrimaryKey val fecha: String,
    val inicioPeriodo: String,
    val nivel: String,
    val notas: String,
    val foto: String = ""
)
