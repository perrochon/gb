// Copyright 2018 Louis Perrochon. All rights reserved

// GBStar deals with anything on the star system wide level

package com.zwsi.gblib

import java.util.*

class GBStar(val universe: GBUniverse) {

    val id: Int
    val uid: Int
    val idxname: Int

    val name: String // name of this system

    private val x: Int // x coordinate
    private val y: Int // y coordinate

    val loc: GBLocation

    var starPlanets: MutableList<GBPlanet> = arrayListOf() // the planets in this system

    var numberOfPlanets = 2 // minimal number of planets

    var starShips: MutableList<GBShip> = arrayListOf() // the ships in this system

    init {
        id = GBData.getNextGlobalId()
        universe.allStars.add(this)
        uid = universe.allStars.indexOf(this)

        idxname = GBData.selectStarNameIdx()
        name = GBData.starNameFromIdx(idxname)


        // TODO fix getStarCoordinates
        val coordinates = getStarCoordinates()
        x = coordinates[0]
        y = coordinates[1]

        loc = GBLocation(x.toFloat(),y.toFloat())
        GBLog.d("Star $name location is ($x,$y)")

        if (universe.numberOfStars > 3) {
            // 2-8 stars. May adjust this later based on star type...
            // if there are 3 or less stars in the Universe, we limit to 2 planets per system, as we are likely testing
            numberOfPlanets += GBData.rand.nextInt(6)
        }

        makePlanets()

        GBLog.d("Made System $name")
    }


    companion object {
        var areas: ArrayList<Int> = ArrayList() // we fill it up on first call to GetStarCoordinates

        // TODO This smells. Caller universe has to clear GBStar's data structue.
        // Also, knowing areas (and keeping them) at a higher level, if they are made hierarchical
        // (Say 4 areas each with 5 sub-areas, then place allStars into sub area) then place one race in each area.
        // Of course for 17 allRaces, this would lead to three levels with 64 sub-sub-areas. Quadratic may be better.

        fun resetStarCoordinates() {
            areas.clear()
        }
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

     fun makePlanets() {
        GBLog.d("Making Planets for star $name")

        for (i in 0 until numberOfPlanets) {
            val p = GBPlanet(i, this)
            starPlanets.add(p)

        }

    }

    // Get random, but universally distributed coordinates for allStars
    // Approach: break up the universe into n areas of equal size, and put one star in each area
    // where n is the smallest square number bigger than numberOfStars. Then shuffle the areas as some will remain
    // empty.


    fun getStarCoordinates(): IntArray {

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
        val areaWidth = GBData.getUniverseMaxX() / dim
        val areaHeight = GBData.getUniverseMaxY() / dim
        val marginX = areaWidth / 10 // no star in the edge of the area
        val marginY = areaHeight / 10 // no star in the edge of the area

        GBLog.d("Adding Star to area " + area + "[" + areaX + "][" + areaY + "] (" + areaWidth + "x" + areaHeight + "){" + marginX + ", " + marginY + "}")

        coordinates[0] = GBData.rand.nextInt(areaWidth - 2 * marginX) + areaX * areaWidth + marginX
        coordinates[1] = GBData.rand.nextInt(areaHeight - 2 * marginY) + areaY * areaHeight + marginY

        return coordinates

    }

}
