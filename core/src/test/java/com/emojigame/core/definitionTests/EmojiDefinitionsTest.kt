package com.emojigame.core.definitionTests

import com.emojigame.core.definitions.Emojis
import org.junit.Assert.*
import org.junit.Test

class EmojiDefinitionsTest {

    @Test
    fun testEmojiTagsAreUnique() {
        val tags = Emojis.all.map { it.tag }
        assertEquals("Tags should be unique", tags.size, tags.toSet().size)
    }

    @Test
    fun testAllDescriptionsAreNotBlank() {
        Emojis.all.forEach {
            it.description?.let { description ->
                assertTrue("Emoji ${it.tag} should have a description", description.isNotBlank())
            }
        }
    }

    @Test
    fun testByTagMappingIsCorrect() {
        Emojis.all.forEach {
            val fromMap = Emojis.byTag[it.tag]
            assertNotNull("Emoji ${it.tag} should be in byTag map", fromMap)
            assertEquals("Emoji ${it.tag} should match byTag lookup", it, fromMap)
        }
    }
}
