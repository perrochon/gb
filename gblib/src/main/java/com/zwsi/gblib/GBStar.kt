// Copyright 2018 Louis Perrochon. All rights reserved

// GBStar deals with anything on the star system wide level

package com.zwsi.gblib

import com.squareup.moshi.JsonClass
import com.zwsi.gblib.GBController.Companion.u
import java.util.*

@JsonClass(generateAdapter = true)
data class GBStar(val id: Int, val uid: Int, val numberOfPlanets: Int, val x: Int, val y: Int) {

    // id is a unique object ID. Not currently used anywhere TODO QUALITY id can probably be removed

    var name: String // name of this system
    var loc: GBLocation

    init {
        name = GBData.starNameFromIdx(GBData.selectStarNameIdx())
        loc = GBLocation(x.toFloat(), y.toFloat())
        GBLog.d("Made System $name at location ($x,$y)")
    }

    @Transient
    var starPlanets: MutableList<GBPlanet> = arrayListOf() // the planets in this system

    // Planets
    var starUidPlanetList: MutableList<Int> =
        Collections.synchronizedList(arrayListOf<Int>()) // UID of ships. Persistent

    val starPlanetsList: List<GBPlanet>
        // PERF ?? Cache the list and only recompute if the hashcode changes.
        get() = Collections.synchronizedList(starUidPlanetList.map { u.planet(it) })

    // Ships
    internal var starUidShipList: MutableList<Int> =
        Collections.synchronizedList(arrayListOf<Int>()) // UID of ships. Persistent

    val starShipList: List<GBShip>
        // PERF ?? Cache the list and only recompute if the hashcode changes.
        get() = Collections.synchronizedList(starUidShipList.map { u.ship(it) })

    fun consoleDraw() {
        println("\n  " + "====================")
        println("  $name System")
        println("  " + "====================")
        println("  The $name system contains ${starUidPlanetList.size} planet(s) and ${starUidShipList.size} ship(s).")

        for (i in starPlanets) {
            i.consoleDraw()
        }
    }
}
