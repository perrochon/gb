// Copyright 2018-2019 Louis Perrochon. All rights reserved

// GBStar deals with system wide level

package com.zwsi.gblib

import com.squareup.moshi.JsonClass
import com.zwsi.gblib.GBController.Companion.u
import kotlin.collections.HashSet

@JsonClass(generateAdapter = true)
data class GBStar(val id: Int, val uid: Int, val numberOfPlanets: Int, val x: Int, val y: Int) {
    // FIXME DELETE id can probably be removed
    // id is a unique object ID. Not currently used anywhere

    var name: String
    var loc: GBLocation  // FIXME NICE pass in a GBLocation instead of (x,y)

    init {
        name = GBData.starNameFromIdx(GBData.selectStarNameIdx())
        loc = GBLocation(x.toFloat(), y.toFloat())
        GBLog.d("Made System $name at location ($x,$y)")
    }

    // Planets
    // PERF ?? Don't save, compute on load. Save happens every turn, re-loads are rare. Smaller file. Likely not worth it.
    internal var starUidPlanetSet: MutableSet<Int> = HashSet<Int>() // UID of planets. Persistent

    internal val starPlanetsList: List<GBPlanet>
        // PERF ?? Cache the list and only recompute if the hashcode changes. Which is rare
        get() = starUidPlanetSet.map { u.planet(it) }

    // Ships
    internal var starUidShipSet: MutableSet<Int> = HashSet<Int>() // UID of ships. Persistent

    internal val starShipList: List<GBShip>
        // PERF ?? Cache the list and only recompute if the hashcode changes.
        get() = starUidShipSet.map { u.ship(it) }

    fun consoleDraw() {
        println("\n  " + "====================")
        println("  $name System")
        println("  " + "====================")
        println("  The $name system contains ${starUidPlanetSet.size} planet(s) and ${starUidShipSet.size} ship(s).")

        for (i in starPlanetsList) {
            i.consoleDraw()
        }
    }
}
