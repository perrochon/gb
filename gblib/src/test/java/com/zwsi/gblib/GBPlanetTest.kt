// Copyright 2018 Louis Perrochon. All rights reserved

// LibTest is running through a big Universe and a longer scenario
//

package com.zwsi.gblib

import org.junit.Test

import org.junit.Assert.*

class GBPlanetTest {

    fun consistent(planet: GBPlanet){
        val universe = GBController.u
        assertTrue(planet.name.length > 0)
        assertEquals(planet.star.universe,universe)

        assertTrue(planet.star.starPlanets.contains(planet))
        assertTrue(universe.allPlanets.contains(planet))

        assertEquals(planet.sid,planet.star.starPlanets.indexOf(planet))
        assertEquals(planet.uid,universe.allPlanets.indexOf(planet))

        var count = 0
        for (star in planet.star.universe.allStars) {
            for (pl in star.starPlanets) {
                if (pl.uid == planet.uid)
                    count++
            }
        }
        assertEquals(1, count)
    }

    @Test
    fun basic() {
        val universe = GBController.makeUniverse()


        for (p in universe.allPlanets) {
            consistent(p)
        }
    }

    @Test
    fun sizes() {
        val universe = GBController.makeUniverse()


        for (p in universe.allPlanets) {
            val width = p.width
            val height = p.height

            assertEquals(width * height, p.size)

            assertEquals(p.sectorX(0), 0)
            assertEquals(p.sectorY(0), 0)

            assertEquals(p.sectorX(width - 1), width - 1)
            assertEquals(p.sectorY(width - 1), 0)

            assertEquals(p.sectorX(width), 0)
            assertEquals(p.sectorY(width), 1)

            assertEquals(p.sectorX(width * height - 1), width - 1)
            assertEquals(p.sectorY(width * height - 1), height - 1)
        }
    }

    @Test
    fun distanceToStar() {
        val universe = GBController.makeUniverse()

        universe.allPlanets.forEach {
            assert(it.loc.getLoc().distance(it.star.loc.getLoc())< (GBData.MaxPlanetOrbit*1.1f), {"Planet too far from star"} )
        }
    }

    @Test
    fun sectorDirections() {
        val universe = GBController.makeUniverse()

        for (p in universe.allPlanets) {
            val width = p.width
            val height = p.height

            assertEquals(width * height, p.size)

            assertEquals(p.east(0), 1)
            assertEquals(p.west(0), width - 1)
            assertEquals(p.north(0), 0)
            assertEquals(p.south(0), width)

            assertEquals(p.east(width * height - 1), width * (height - 1))
            assertEquals(p.west(width * height - 1), width * height - 2)
            assertEquals(p.north(width * height - 1), width * (height - 1) - 1)
            assertEquals(p.south(width * height - 1), width * height - 1)

            assertEquals(p.west(width * (height - 1)), width * height - 1)
            assertEquals(p.east(width * height - 2), width * height - 1)
            assertEquals(p.south(width * (height - 1) - 1), width * height - 1)
        }
    }

    @Test
    fun planetMoves() {

        val universe = GBController.makeUniverse()

        for (p in universe.allPlanets) {

            val rt1 = p.loc.getSLocP()
            val xy1 = p.loc.getSLocC()

            p.movePlanet()

            var rt2 = p.loc.getSLocP()
            var xy2 = p.loc.getSLocC()

            assertEquals(rt1.r, rt2.r)
            assert(xy1.distance(xy2) < 0.99) // Or pods can never catch up
        }
    }

}
