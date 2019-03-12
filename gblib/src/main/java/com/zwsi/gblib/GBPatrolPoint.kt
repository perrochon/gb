// Copyright 2018-2019 Louis Perrochon. All rights reserved

// GBPlanet deals with planetary level

package com.zwsi.gblib

import com.squareup.moshi.JsonClass
import com.zwsi.gblib.GBController.Companion.u
import java.util.*

@JsonClass(generateAdapter = true)
data class GBPatrolPoint(val uid: Int, val sid: Int, val uidStar: Int, var loc: GBLocation) {
    // TODO DELETE sid can probably be removed.
    // sid is the "Star ID" (aka orbit), where in the order of planets of the parent star is this 0..n

    val star: GBStar
        get() = u.star(uidStar) // FIXME PERSISTENCE These return universe objects, not vm objects...  only return UID

    // Orbit Ships
    var orbitUidShips: MutableSet<Int> = HashSet() // UID of shipsData. Persistent

    internal val orbitShips: List<GBShip>
        // PERF ?? Cache the list and only recompute if the hashcode changes. How often is this used....
        get() = orbitUidShips.map { u.ship(it) }

    init {
    }

    fun movePatrolPoint() {
        // TODO Use Keplers law for planet movement along ellipses
        // e.g. http://www.sjsu.edu/faculty/watkins/orbital.htm
        // Need to make sure that no planet goes faster than pods, or pods never catch up...
        // Speed of pods is 1, so angular speed cannot be faster than 1/r

        loc = computePatrolPointPositions(1)
    }

    fun computePatrolPointPositions(turns: Int): GBLocation {
        val rt = loc.getSLocP()
        val speed = 1 / (rt.r + 10) / 2
        return GBLocation(
            u.star(uidStar),
            rt.r,
            rt.t + speed * turns
        ) // y points down, anti-clockwise is negative angles...
    }

    fun doPatrolPoint() {
        movePatrolPoint()

    }
}
