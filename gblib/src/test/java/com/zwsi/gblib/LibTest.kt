// Copyright 2018 Louis Perrochon. All rights reserved

// LibTest is running through a big Universe and a longer scenario
//

package com.zwsi.gblib

import org.junit.Test

import org.junit.Assert.*

class LibTest {

    fun consistent(universe: GBUniverse) {
        assertEquals(universe.allStars.size, universe.numberOfStars)
        assertEquals(universe.allRaces.size, universe.numberOfRaces)
        assertEquals(universe.allStars[0].starPlanets[0], universe.allPlanets[0])
        assertEquals(universe.allStars[0].starPlanets[1], universe.allPlanets[1])
    }

    @Test
    fun makeSmallUniverse() {
        var universe = GBUniverse(3,2)
        assertEquals(3, universe.allStars.size)
        assertEquals(6, universe.allPlanets.size)
        assertEquals(2, universe.allRaces.size)
        assertEquals( 6, universe.allShips.size)
        assertEquals(100, universe.allPlanets[0].population)
        assertEquals(100, universe.allPlanets[2].population)
        consistent(universe)

    }

    @Test
    fun doSmallUniverse() {
        val universe = GBUniverse(3,2)
        universe.doUniverse()
        assertEquals(3, universe.allStars.size)
        assertEquals(6, universe.allPlanets.size)
        assertEquals(2, universe.allRaces.size)
        assertEquals( 6, universe.allShips.size)
        consistent(universe)
    }

    val big = 33

    @Test
    fun makeBigUniverse() {
        var universe = GBUniverse(big,2)
        assertEquals(big, universe.allStars.size)
        assertEquals(big*2, universe.allPlanets.size)
        assertEquals(2, universe.allRaces.size)
        consistent(universe)
    }

    @Test
    fun doBigUniverse1000() {
        val universe = GBUniverse(big,2)
        for (i in 1..big)
            universe.doUniverse()
        assertEquals(big, universe.allStars.size)
        assertEquals(big*2, universe.allPlanets.size)
        assertEquals(2, universe.allRaces.size)
        consistent(universe)
    }

    @Test
    fun landPopulation() {
        val universe = GBUniverse(3,2)
        universe.landPopulation(universe.allPlanets[1], universe.allRaces[0].uid, 55)
        assertEquals(55, universe.allStars[0].starPlanets[1].population)
    }

}
