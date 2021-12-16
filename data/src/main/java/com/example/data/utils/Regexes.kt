package com.example.data.utils

object Regexes {
    /**
     * Regex that matches 1st letter and 8 following numbers
     * */
    val SEAL_NUMBER_REGEX get() = "[a-zA-Z][0-9]{8}$".toRegex()
}