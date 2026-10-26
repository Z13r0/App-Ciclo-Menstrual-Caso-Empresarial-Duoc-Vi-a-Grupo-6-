package com.myciclo.app.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Utilidades para manejo, parseo y validación de fechas en la app myCiclo.
 * Garantiza el uso del formato estándar ISO (yyyy-MM-dd).
 */
object FechaUtils {

    private val formatterISO = DateTimeFormatter.ISO_LOCAL_DATE
    private val formatterDisplay = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    /**
     * Valida si una cadena de texto tiene un formato de fecha válido (yyyy-MM-dd o dd/MM/yyyy).
     */
    fun esValida(fechaStr: String): Boolean {
        if (fechaStr.isBlank()) return false
        return parsear(fechaStr) != null
    }

    /**
     * Parsea una cadena de fecha de forma segura intentando formatos ISO y dd/MM/yyyy.
     * Retorna null si la fecha no es válida en lugar de lanzar una excepción.
     */
    fun parsear(fechaStr: String): LocalDate? {
        if (fechaStr.isBlank()) return null
        return try {
            LocalDate.parse(fechaStr, formatterISO)
        } catch (_: DateTimeParseException) {
            try {
                LocalDate.parse(fechaStr, formatterDisplay)
            } catch (_: DateTimeParseException) {
                null
            }
        }
    }

    /**
     * Convierte un LocalDate a su formato ISO estándar (yyyy-MM-dd).
     */
    fun aFormatoIso(date: LocalDate): String {
        return date.format(formatterISO)
    }

    /**
     * Convierte un LocalDate a su formato de visualización (dd/MM/yyyy).
     */
    fun aFormatoDisplay(date: LocalDate): String {
        return date.format(formatterDisplay)
    }

    /**
     * Retorna la fecha actual formateada en ISO (yyyy-MM-dd).
     */
    fun hoyIso(): String {
        return LocalDate.now().format(formatterISO)
    }
}
