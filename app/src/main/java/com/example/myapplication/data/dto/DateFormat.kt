package com.example.myapplication.data.dto

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private const val ISO_SECONDS_LENGTH = 19

private fun parseIso(iso: String): Date? = try {
    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        .apply { timeZone = TimeZone.getTimeZone("UTC") }
        .parse(iso.take(ISO_SECONDS_LENGTH))
} catch (_: Exception) {
    null
}

/** "2026-10-02T09:35:00.000Z" -> "Oct 2, 2026" (cadena vacía si no se puede interpretar). */
fun String?.toDisplayDate(): String {
    val date = this?.let(::parseIso) ?: return ""
    return SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date)
}

fun String?.toYear(): Int = this?.take(4)?.toIntOrNull() ?: 0
