// Copyright 2018 Louis Perrochon. All rights reserved

// GBStar deals with anything on the star system wide level


package com.zwsi.gblib

class GBStar internal constructor(
    var uId: Int // sId is the "starID" (aka orbit), which planet of the parent star is this 0..
) {


    private val nameIdx: Int
    val name: String // name of this system
    var index: Int = 0
        internal set // which position in Universe's star array

    private val numberOfPlanets = 2 // how many Planets in this solar Systems

    val x: Int // x coordinate
    val y: Int // y coordinate

    var planetsArray =
        arrayOfNulls<GBPlanet>(numberOfPlanets) // the solar Systems // TODO make private an return copy in getter

    init {
        nameIdx = GBData.selectStarNameIdx()
        name = GBData.starNameFromIdx(nameIdx)
        makePlanets()

        GBDebug.l3("Made System $name")

        val coordinates = GBData.getStarCoordinates()
        x = coordinates[0]
        y = coordinates[1]

    }

    fun consoleDraw() {
        println("\n  " + "====================")
        println("  $name System")
        println("  " + "====================")
        println("  The $name system contains $numberOfPlanets planet(s).")

        for (i in planetsArray) {

            i?.consoleDraw()
        }

    }

    private fun makePlanets() {
        GBDebug.l3("Making Planets for star $name")

        for (i in planetsArray.indices) {
            planetsArray[i] = GBPlanet(i)
        }

    }
}
