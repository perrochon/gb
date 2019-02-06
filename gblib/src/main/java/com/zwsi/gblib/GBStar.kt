// Copyright 2018 Louis Perrochon. All rights reserved

// GBStar deals with anything on the star system wide level

package com.zwsi.gblib

import com.squareup.moshi.JsonClass
import com.zwsi.gblib.GBController.Companion.u
import java.util.*

@JsonClass(generateAdapter = true)
data class GBStar(val id: Int, val uid: Int, val numberOfPlanets: Int, val x: Int, val y : Int) {

    // int is a unique object ID. Not currently used anywhere TODO QUALITY id can probably be removed

    val name: String // name of this system
    val loc: GBLocation

    @Transient
    var starPlanets: MutableList<GBPlanet> = arrayListOf() // the planets in this system

    // TODO PERSISTENCE Save these, or rebuild on loading?
    // If they are ships, as opposed to UIDs, need to rebuild, as the old objects will be gone...    @Transient
    internal val starShips: MutableList<GBShip> =
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
        println("  The $name system contains $numberOfPlanets planet(s).")

        for (i in starPlanets) {

            i.consoleDraw()
        }

    }



}
