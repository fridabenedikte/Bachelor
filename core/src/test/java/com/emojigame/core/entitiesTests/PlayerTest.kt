package com.emojigame.core.entitiesTests

import com.emojigame.core.data.Platform
import com.emojigame.core.data.PlatformTags
import com.emojigame.core.entities.Player
import com.emojigame.core.util.Vector2f
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class PlayerTest {

    @Test
    fun testGravity() {
        val player = Player(Vector2f(100f, 500f), "Player")
        val gravity = Vector2f(0f, 3.0f)
        player.applyGravity(gravity)
        player.setTarget(Vector2f(200f, 500f))

        val ground = Platform("ground", Vector2f(0f, 500f), 1000f, 400f, PlatformTags.Ground, true)

        player.update(listOf(ground))

        assertEquals(
            "When grounded, vertical speed should not be affected by gravity",
            0f,
            player.fetchSpeed().y,
            0.1f
        )

        player.update(emptyList()) // Remove ground

        assertEquals(
            "When not grounded, vertical speed should increase by gravity",
            gravity.y,
            player.fetchSpeed().y,
            0.1f
        )
    }

    @Test
    fun testPause() {
        val player = Player(Vector2f(100f, 500f), "Player")
        player.setTarget(Vector2f(200f, 500f))
        player.isPaused = true
        val ground = Platform("ground", Vector2f(0f, 500f), 1000f, 400f, PlatformTags.Ground, true)
        player.update(listOf(ground))

        val beforePos = player.pos.copy()
        assertEquals(
            "Position should not change while paused",
            beforePos,
            player.pos
        )
        assertEquals(
            "Movement state should remain IDLE while paused",
            Player.MovementState.IDLE,
            player.movementState
        )
    }

    @Test
    fun testStopAtTarget() {
        val player = Player(Vector2f(0f, 0f), "Player")
        val nearTarget = Vector2f(1f, 0f)
        player.setTarget(nearTarget)
        val ground = Platform("ground", Vector2f(0f, 0f), 100f, 400f, PlatformTags.Ground, true)
        player.update(listOf(ground))

        assertEquals(
            "Player should snap exactly to target x",
            nearTarget.x,
            player.pos.x,
            0.1f
        )
        assertEquals(
            "Player should snap exactly to target y",
            nearTarget.y,
            player.pos.y,
            0.1f
        )
        assertEquals(
            "Movement state should become IDLE after snapping",
            Player.MovementState.IDLE,
            player.movementState
        )
    }

    @Test
    fun testPlayerMovesTowardsTarget() {
        val player = Player(
            pos = Vector2f(0f, 500f),
            tag = "Player"
        )

        val target = Vector2f(100f, 500f)
        player.setTarget(target)

        val ground = Platform(
            id = "ground",
            pos = Vector2f(-1000f, 600f),
            width = 3000f,
            height = 400f,
            tag = PlatformTags.Ground,
            isEnabled = true
        )

        player.update(listOf(ground))

        val startX = player.pos.x

        var steps = 0
        val maxSteps = 100
        while (steps < maxSteps && player.movementState != Player.MovementState.IDLE) {
            player.update(listOf(ground))
            steps++
        }

        val endX = player.pos.x

        println("Final pos: $endX")

        assertTrue("Player should have moved towards the target (startX=$startX, endX=$endX)", endX > startX)
        assertTrue(
            "Player should have reached near the target (target.x=${target.x}, pos.x=$endX)",
            abs(target.x - endX) <= 2f
        )
    }

    @Test
    fun testStopsWhenBlocked() {
        val player = Player(Vector2f(0f, 500f), "Player")
        val target = Vector2f(100f, 500f)

        player.setTarget(target)

        val blockingPlatform = Platform(
            id = "block",
            pos = Vector2f(5f, 500f),
            width = 50f,
            height = 100f,
            tag = PlatformTags.Ground,
            isEnabled = true
        )

        repeat(10) { player.update(listOf(blockingPlatform)) }

        assertTrue(
            "isBlocked should be true when about to collide",
            player.getIsBlocked()
        )
        assertTrue(
            "Player should not move through the block",
            player.pos.x < blockingPlatform.pos.x + blockingPlatform.width
        )
    }
}
