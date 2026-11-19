package com.example.myapplication.data

/** Motivos con los que un usuario puede reportar una reseña. [value] es lo que se guarda en Firestore. */
enum class ReportReason(val value: String) {
    SPAM("spam"),
    OFFENSIVE("offensive"),
    FAKE("fake"),
    OTHER("other");

    companion object {
        fun fromValue(value: String?): ReportReason? = entries.firstOrNull { it.value == value }
    }
}
