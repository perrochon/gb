// Copyright 2018 Louis Perrochon. All rights reserved

// GBStar deals with anything on the star system wide level

package com.zwsi.gblib

import com.squareup.moshi.JsonClass
import com.zwsi.gblib.GBController.Companion.u
import java.util.*

@JsonClass(generateAdapter = true)
data class GBStar(val id: Int, val uid: Int, val numberOfPlanets: Int, val x: Int, val y : Int) {

    // int is a unique object ID. Not currently used anywhere TODO QUALITY id can probably be removed

    var name: String // name of this system
    var loc: GBLocation

    @Transient
    var starPlanets: MutableList<GBPlanet> = arrayListOf() // the planets in this system

    // FIXME PERSISTENCE Store UID. Otherwise we store each ship multiple times.
    internal var starShips: MutableList<GBShip> =
        Collections.synchronizedList(arrayListOf<GBShip>()) // the ships in this system

    @Transient
    internal var lastStarShipsUpdate = -1
    @Transient
    internal var starShipsList = starShips.toList()

    init {
        name = GBData.starNameFromIdx(GBData.selectStarNameIdx())
        loc = GBLocation(x.toFloat(), y.toFloat())
        GBLog.d("Star $name location is ($x,$y)")

        GBLog.d("Made System $name")
    }

    fun getStarShipsList(): List<GBShip> {
        // TODO need smarter cache invalidation. But it's also cheap to produce this list.
         if (u.turn > lastStarShipsUpdate) {
            starShipsList = starShips.toList().filter { it.health > 0 }
            lastStarShipsUpdate = u.turn
        }
        return starShipsList
    }

    fun consoleDraw() {
        println("\n  " + "====================")
        println("  $name System")
        println("  " + "====================")
        println("  The $name system contains ${starPlanets.size} planet(s) and ${starShips.size} ship(s).")

        for (i in starPlanets) {
            i.consoleDraw()
        }
    }
}
