package com.emojigame.core.entitiesTests

import com.emojigame.core.entities.Entity
import com.emojigame.core.util.Vector2f
import org.junit.Assert.*
import org.junit.Test

class EntityTest {

    private class TestEntity(
        override val pos: Vector2f,
        override val tag: String,
        override val width: Float,
        override val height: Float
    ) : Entity()

    @Test
    fun testColliding() {
        val entity1 = TestEntity(Vector2f(0f, 0f), "Entity1", 50f, 50f)
        val entity2 = TestEntity(Vector2f(25f, 25f), "Entity2", 50f, 50f)
        val entity3 = TestEntity(Vector2f(100f, 100f), "Entity3", 50f, 50f)

        assertTrue("Overlapping entities should collide", entity1.collidesWith(entity2))
        assertFalse("Not overlapping, entities should not collide", entity1.collidesWith(entity3))
    }

}
