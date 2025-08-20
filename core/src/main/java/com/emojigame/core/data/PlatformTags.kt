package com.emojigame.core.data

/**
 * Enum representing the logical category or type of a platform or background element.
 *
 * These tags are used to match platform behaviors, colors, activation rules, or visuals
 * based on a symbolic name rather than hardcoded types.
 *
 * @property tag The string representation used in XML or component references.
 */
enum class PlatformTags(val tag: String) {
    /** Solid ground surface (e.g., grass, dirt) */
    Ground("ground"),

    /** Bridge platform, often activatable or movable */
    Bridge("bridge"),

    /** Decorative or non-collidable water area */
    Water("water"),

    Obstacle("obstacle"),

    /** Fallback tag for unrecognized inputs */
    Unknown("unknown"), ;

    companion object {
        /**
         * Converts a string tag into a [PlatformTags] enum value.
         *
         * @param tag The raw tag string (e.g., from XML or components).
         * @return A matching [PlatformTags] value, or [Unknown] if no match.
         */
        fun from(tag: String): PlatformTags {
            return entries.find { it.tag == tag } ?: Unknown
        }
    }

    fun isBlocking(): Boolean {
        return this == Ground || this == Obstacle
    }
}
