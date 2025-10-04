package com.example.vehiclealert.util

object PlateUtil {
    private val plateRegex = Regex("[A-Z0-9]{2,4}[- ]?[A-Z0-9]{1,2}[- ]?[A-Z0-9]{1,4}")

    fun normalize(input: String): String = input
        .trim()
        .uppercase()
        .replace(" ", "")
        .replace("-", "")

    fun extractLikelyPlate(text: String): String? {
        val upper = text.uppercase()
        val match = plateRegex.find(upper) ?: return null
        return normalize(match.value)
    }
}
