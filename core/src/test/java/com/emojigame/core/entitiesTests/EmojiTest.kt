package com.emojigame.core.entitiesTests

import com.emojigame.core.entities.Emoji
import org.junit.Assert.*
import org.junit.Test

class EmojiTest {

    @Test
    fun testAssignEmojiIdGeneratesUniqueId() {
        val emoji = Emoji(tag = "TestEmoji")
        val newEmoji = emoji.assignEmojiId()

        assertNotNull("Assigned ID should not be null", newEmoji.id)
        assertTrue("Assigned ID should not be empty", newEmoji.id!!.isNotEmpty())
        assertNotEquals("New emoji should have a different ID", emoji.id, newEmoji.id)
    }

    @Test
    fun testToStringReturnsCorrectFormat() {
        val emoji = Emoji(tag = "Laughing")
        val result = emoji.toString()
        assertTrue(result.contains("tag: Laughing"))
    }
}