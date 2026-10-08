package com.myciclo.app.contenido

// una guia o pregunta frecuente, viene del servidor (spring boot)
data class Guia(
    val id: Int,
    val titulo: String,
    val contenido: String,
    val categoria: String
)
