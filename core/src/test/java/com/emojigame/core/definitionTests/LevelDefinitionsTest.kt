package com.emojigame.core.definitionTests

import com.emojigame.core.definitions.LevelDefinitions
import com.emojigame.core.data.Component
import com.emojigame.core.definitions.Emojis
import org.junit.Assert.*
import org.junit.Test

class LevelDefinitionsTest {

    @Test
    fun testGetReturnsCorrectLevel() {
        val level = LevelDefinitions.Level3
        val result = LevelDefinitions.get("level3")
        assertNotNull("Expected to retrieve Level3", result)
        assertEquals("Level name should match", level.name, result!!.name)
    }

    @Test
    fun testGetReturnsNullForUnknownName() {
        val result = LevelDefinitions.get("unknown-level")
        assertNull("Should return null for unknown level name", result)
    }

    @Test
    fun testAllLevelsHaveValidPlayerPosition() {
        LevelDefinitions.allLevels.forEach { level ->
            assertNotNull("Player position should not be null in ${level.name}", level.player.pos)
        }
    }

    @Test
    fun testAllComponentsUseValidEmojiTags() {
        val validTags = Emojis.byTag.keys

        LevelDefinitions.allLevels.forEach { level ->
            level.components.forEach { component ->
                when (component) {
                    is Component.EmojiActivatesPlatform -> {
                        assertTrue(
                            "Invalid emoji tag '${component.emoji.tag}' in ${level.name}",
                            validTags.contains(component.emoji.tag)
                        )
                    }

                    is Component.EmojiSequenceRemovesPlatform -> {
                        component.emojiSequence.forEach { tag ->
                            assertTrue(
                                "Invalid emoji tag '$tag' in sequence in ${level.name}",
                                validTags.contains(tag.tag)
                            )
                        }
                    }

                    else -> {
                        // No emoji tag to validate
                    }
                }
            }
        }
    }

    @Test
    fun testAllLevelsAreAccessible() {
        with(LevelDefinitions) {
            allLevels.forEach {
                assert(it.name.startsWith("level")) { "Level name '${it.name}' should start with 'level'" }
            }

            assertEquals(allLevels.size, allLevels.distinctBy { it.name }.size)

            assertEquals(Level3, get("level3"))
        }
    }

    @Test
    fun testAllPlatforms() {
        LevelDefinitions.allLevels.forEach { level ->
            level.platforms.forEach { platform ->
                    assertNotNull("Platform, ${platform.id}, position should not be null in ${level.name}", platform.pos)
                    assertNotNull("Platform, ${platform.id}, width should not be null in ${level.name}", platform.width)
                    assertNotNull("Platform, ${platform.id}, position should not be null in ${level.name}", platform.height)
                }
            }
        }
    }


