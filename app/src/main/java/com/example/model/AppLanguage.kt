package com.example.model

enum class AppLanguage(
    val code: String,
    val nativeName: String,
    val englishName: String,
    val flag: String
) {
    ES("es", "Español", "Spanish", "🇪🇸"),
    EN("en", "English", "English", "🇺🇸"),
    ZH("zh", "中文 (简体)", "Chinese", "🇨🇳"),
    JA("ja", "日本語", "Japanese", "🇯🇵"),
    FR("fr", "Français", "French", "🇫🇷"),
    DE("de", "Deutsch", "German", "🇩🇪"),
    PT("pt", "Português", "Portuguese", "🇧🇷"),
    IT("it", "Italiano", "Italian", "🇮🇹"),
    RU("ru", "Русский", "Russian", "🇷🇺"),
    KO("ko", "한국어", "Korean", "🇰🇷");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return values().firstOrNull {
                it.code.equals(code, ignoreCase = true) || it.name.equals(code, ignoreCase = true)
            } ?: ES
        }
    }
}
