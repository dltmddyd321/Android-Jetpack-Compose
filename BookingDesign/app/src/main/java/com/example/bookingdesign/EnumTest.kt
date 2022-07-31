package com.example.bookingdesign

enum class EnumTest {
    Naver,
    Google,
    Yahoo,
    Daum
}

fun getPortalType(s : String) {
    when (s) {
        "naver" -> EnumTest.Naver
        "google" -> EnumTest.Google
        "yahoo" -> EnumTest.Yahoo
        else -> EnumTest.Daum
    }
}

enum class Color(val rgb: Int, val colorName: String) {
    RED(0xFF0000, "red"),
    GREEN(0x00FF00, "green"),
    BLUE(0x0000FF, "blue")
}

interface Languages {
    fun getLang() : String
}

enum class GetLanguage(s: String) : Languages {
    JAVA("java") {
        override fun getLang(): String = "JAVA"
    },
    KOTLIN("kotlin") {
        override fun getLang(): String = "KOTLIN"
    },
    PYTHON("python") {
        override fun getLang(): String = "PYTHON"
    }
}