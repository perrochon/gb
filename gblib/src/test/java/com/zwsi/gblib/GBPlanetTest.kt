// Copyright 2018 Louis Perrochon. All rights reserved

// LibTest is running through a big Universe and a longer scenario
//

package com.zwsi.gblib

import org.junit.Test

import org.junit.Assert.*

class GBPlanetTest {

    fun consistent(planet: GBPlanet){
        assert(planet.name.length > 0)
        assertEquals(planet.uid,planet.star.universe.allPlanets.indexOf(planet))

        assert(planet.star.starPlanets.contains(planet))

        var listed = 0
        for (star in planet.star.universe.allStars) {
            for (pl in star.starPlanets) {
                if (pl.uid == planet.uid)
                    listed++
            }
        }
        assertEquals(1, listed)
    }


    @Test
    fun coordinates() {
        val universe = GBUniverse(3,2)
        val p = universe.allPlanets[0]
        val width=p.width
        val height = p.height
        assertEquals(width*height, p.size)

        assertEquals(p.sectorX(0),0)
        assertEquals(p.sectorY(0),0)

        assertEquals(p.sectorX(width-1),width-1)
        assertEquals(p.sectorY(width-1),0)

        assertEquals(p.sectorX(width),0)
        assertEquals(p.sectorY(width),1)

        assertEquals(p.sectorX(width*height-1),width-1)
        assertEquals(p.sectorY(width*height-1),height-1)

        consistent(p)
    }

    @Test
    fun directions() {
        val universe = GBUniverse(3,2)
        val p = universe.allPlanets[0]
        val width=p.width
        val height = p.height
        assertEquals(width*height, p.size)

        assertEquals(p.east(0), 1)
        assertEquals(p.west(0), width-1)
        assertEquals(p.north(0), 0)
        assertEquals(p.south(0), width)

        assertEquals(p.east(width*height-1), width*(height-1))
        assertEquals(p.west(width*height-1), width*height-2)
        assertEquals(p.north(width*height-1), width*(height-1)-1)
        assertEquals(p.south(width*height-1), width*height-1)

        assertEquals(p.west(width*(height-1)), width*height-1)
        assertEquals(p.east(width*height-2), width*height-1)
        assertEquals(p.south(width*(height-1)-1), width*height-1)

        consistent(p)
    }

}
