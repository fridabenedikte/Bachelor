package com.emojigame.core.util

/**
 * Logger is a utility object for logging messages with different severity levels.
 * It provides a unified way to log messages with additional context such as location of the caller
 * (caller = the function that calls Logger
 *
 * Usage:
 * - Logger.d { "Debug message" } - Logs a DEBUG message.
 * - Logger.i { "Info message" } - Logs an INFO message.
 * - Logger.w { "Warning message" } - Logs a WARN message.
 * - Logger.e { "Error message" } - Logs an ERROR message.
 *
 * Each log message includes an emoji representing the log level, the log level name, the caller's
 * location, and the message. Log level is standardized language for the type of logging message.
 */
object Logger {
    fun log(
        level: LogLevel,
        message: () -> String,
    ) {
        val stackTrace = Throwable().stackTrace
        val caller = stackTrace.getOrNull(2) // 2nd item is the actual caller
        val location =
            caller?.let {
                "${it.fileName}:${it.lineNumber}"
            } ?: "Unknown location"

        val emoji =
            when (level) {
                LogLevel.DEBUG -> "🐛"
                LogLevel.INFO -> "ℹ️"
                LogLevel.WARN -> "⚠️"
                LogLevel.ERROR -> "❌"
            }

        println("[$emoji ${level.name}] [$location] → ${message()}")
    }

    fun d(message: () -> String) = log(LogLevel.DEBUG, message)

    fun i(message: () -> String) = log(LogLevel.INFO, message)

    fun w(message: () -> String) = log(LogLevel.WARN, message)

    fun e(message: () -> String) = log(LogLevel.ERROR, message)
}
