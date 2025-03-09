package com.avcialper.lemur.util.constant

enum class Theme(val value: String) {
    SYSTEM_DEFAULT("system_default"),
    LIGHT("light"),
    DARK("dark");

    companion object {
        fun fromString(theme: String?): Theme = entries.find { it.value == theme } ?: SYSTEM_DEFAULT
    }
}