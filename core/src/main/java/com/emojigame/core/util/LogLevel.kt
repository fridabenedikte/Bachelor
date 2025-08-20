package com.emojigame.core.util

/**
 * LogLevel represents the severity of the log message.
 *
 * - DEBUG: Used for detailed debugging information. Useful for developing, but typically removed
 *      before pushing to master.
 * - INFO: Used for informational messages that highlight the progress of the application. Can be
 *      left in as they can be usefor to understand lifecycle and flow.
 * - WARN: Used for potentially harmful situations. Useful for example in situations that typically
 *      should not occur
 * - ERROR: Used for error events that might still allow the application to continue running.
 */
enum class LogLevel {
    DEBUG,
    INFO,
    WARN,
    ERROR
}
