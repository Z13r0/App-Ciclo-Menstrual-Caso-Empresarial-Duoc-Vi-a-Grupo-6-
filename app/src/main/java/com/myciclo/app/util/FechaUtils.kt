package com.myciclo.app.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object FechaUtils {

    private val formatterISO = DateTimeFormatter.ISO_LOCAL_DATE
    private val formatterDisplay = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    fun esValida(fechaStr: String): Boolean {
        if (fechaStr.isBlank()) return false
        return parsear(fechaStr) != null
    }

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

    fun aFormatoIso(date: LocalDate): String = date.format(formatterISO)

    fun aFormatoDisplay(date: LocalDate): String = date.format(formatterDisplay)

    fun hoyIso(): String = LocalDate.now().format(formatterISO)
}