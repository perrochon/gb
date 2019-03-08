// Copyright 2018-2019 Louis Perrochon. All rights reserved

// GBStar deals with system wide level

package com.zwsi.gblib

import com.squareup.moshi.JsonClass
import com.zwsi.gblib.GBController.Companion.u
import kotlin.collections.HashSet

@JsonClass(generateAdapter = true)
data class GBStar(val uid: Int, val numberOfPlanets: Int, var loc: GBLocation) {

    var name: String

    init {
        name = GBData.starNameFromIdx(GBData.selectStarNameIdx())
        GBLog.d("Made System $name at location (${loc.x},${loc.y})")
    }

    // Planets
    // PERF ?? Don't keep materialized view, but compute on load and cache. Save happens every turn, but re-loads are rare.
    // Smaller file. Likely not worth it.
    var starUidPlanets: MutableSet<Int> = HashSet<Int>() // UID of planets. Persistent

    internal val starPlanets: List<GBPlanet>
        // PERF ?? Likely bad candidate. Cache the list and only recompute if the hashcode changes. Which requires Death Stars
        get() = starUidPlanets.map { u.planet(it) }

    // Ships
    var starUidShips: MutableSet<Int> = HashSet<Int>() // UID of shipsData. Persistent

    internal val starShips: List<GBShip>
        // PERF ?? Likely good candidate. Cache the list and only recompute if the hashcode changes.
        // This list changes frequently on update and is read frequently when drawing ( 30 times/second ) (no updates)
        get() = starUidShips.map { u.ship(it) }

    fun consoleDraw() {
        println("\n  " + "====================")
        println("  $name System")
        println("  " + "====================")
        println("  The $name system contains ${starUidPlanets.size} planet(s) and ${starUidShips.size} ship(s).")

        for (i in starPlanets) {
            i.consoleDraw()
        }
    }
}
