package com.myciclo.app.registro

import androidx.room.Entity

// Una fila por día y cuenta; evita colisiones entre usuarias.
// Las fechas van como texto "yyyy-MM-dd".
// foto guarda la ruta del archivo dentro de la app (vacía si no hay foto).
@Entity(tableName = "registros", primaryKeys = ["userId", "fecha"])
data class RegistroEntity(
    val userId: String,
    val fecha: String,
    val inicioPeriodo: String,
    val nivel: String,
    val notas: String,
    val foto: String = "",
    val notasPrivacidad: String = ""
)