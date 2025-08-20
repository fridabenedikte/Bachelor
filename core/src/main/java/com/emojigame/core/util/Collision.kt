package com.emojigame.core.util

/**
 * Utility class for handling collision detection between shapes.
 */
object Collision {
    /**
     * Checks whether two AABBs intersect.
     *
     * @param a First AABB
     * @param b Second AABB
     * @return True if the AABBs overlap
     */
    fun aabbVsAabb(
        a: AABB,
        b: AABB,
    ): Boolean {
        return a.intersects(b)
    }

    /**
     * Checks if a ray intersects an AABB using the slab method.
     *
     * @param ray The ray to test.
     * @param aabb The axis-aligned bounding box.
     * @return True if the ray intersects the AABB within the ray's length.
     */
    fun rayVsAabb(
        ray: Ray,
        aabb: AABB,
    ): Boolean {
        val invDirX = 1f / ray.direction.x
        val invDirY = 1f / ray.direction.y

        val t1 = (aabb.left - ray.origin.x) * invDirX
        val t2 = (aabb.right - ray.origin.x) * invDirX
        val t3 = (aabb.top - ray.origin.y) * invDirY
        val t4 = (aabb.bottom - ray.origin.y) * invDirY

        val tmin = maxOf(minOf(t1, t2), minOf(t3, t4))
        val tmax = minOf(maxOf(t1, t2), maxOf(t3, t4))

        return tmax >= maxOf(tmin, 0f) && tmin <= ray.length
    }
}
