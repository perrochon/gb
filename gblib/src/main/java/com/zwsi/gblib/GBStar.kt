// Copyright 2018 Louis Perrochon. All rights reserved

// GBStar deals with anything on the star system wide level


package com.zwsi.gblib

import java.util.ArrayList

class GBStar {

    var uId: Int
    var universe: GBUniverse

    //
    internal constructor(uId: Int, GBUniverse: GBUniverse) {
        this.uId = uId
        this.universe = GBUniverse
        this.planetsArray = arrayOfNulls<GBPlanet>(numberOfPlanets)
        id = GBData.getNextGlobalId()
        nameIdx = GBData.selectStarNameIdx()
        name = GBData.starNameFromIdx(nameIdx)
        makePlanets()
        GBDebug.l3("Made System $name")
        val coordinates = getStarCoordinates()
        x = coordinates[0]
        y = coordinates[1]
    }

    private val id: Int
    private val nameIdx: Int
    val name: String // name of this system
    var index: Int = 0
        internal set // which position in Universe's star array

    private val numberOfPlanets = 2 // how many Planets in this solar Systems

    val x: Int // x coordinate
    val y: Int // y coordinate

    var planetsArray: Array<GBPlanet?>
    // the solar Systems // TODO make private an return copy in getter


    companion object {
        var areas: ArrayList<Int> = ArrayList() // we fill it up on first call to GetStarCoordinates

        // TODO This smells. Caller universe has to clear GBStar's data structue.
        // Also, knowing areas (and keeping them) at a higher level, if they are made hierarchical
        // (Say 4 areas each with 5 sub-areas, then place stars into sub area) then place one race in each area.
        // Of course for 17 races, this would lead to three levels with 64 sub-sub-areas. Quadratic may be better.

        fun resetStarCoordinates() { areas.clear() }
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

    // Get random, but universally distributed coordinates for stars
    // Approach: break up the universe into n areas of equal size, and put one star in each area
    // where n is the smallest square number bigger than numberOfStars. Then shuffle the areas as some will remain
    // empty.


    fun getStarCoordinates() : IntArray {

        val nos = universe.getNumberOfStars().toDouble()
        val dim = java.lang.Math.ceil(java.lang.Math.sqrt(nos)).toInt()

        if (areas.isEmpty()) {

            // TODO Does this work? areas = IntArray(dim * dim) { i -> i-1}

            for (i in 0 until dim * dim) {
                areas.add(i, i)
            }
            areas.shuffle()
        }

        val coordinates = IntArray(size = 2)
        val area = areas.removeAt(0)

        val areaX = area % dim   // coordinates of the chosen area
        val areaY = area / dim   // coordinates of the chosen area
        val areaWidth = GBData.getUniverseMaxX()/ dim
        val areaHeight = GBData.getUniverseMaxY() / dim
        val marginX = areaWidth / 10 // no star in the edge of the area
        val marginY = areaHeight / 10 // no star in the edge of the area

        GBDebug.l3("Adding Star to area "+area+"["+areaX+"]["+areaY+"] ("+areaWidth+"x"+areaHeight+"){"+marginX+", " + marginY+ "}")

        coordinates[0] = GBData.rand.nextInt(areaWidth - 2*marginX) + areaX * areaWidth + marginX
        coordinates[1] = GBData.rand.nextInt(areaHeight - 2*marginY) + areaY * areaHeight + marginY

        return coordinates

    }

}
