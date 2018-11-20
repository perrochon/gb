// Copyright 2018 Louis Perrochon. All rights reserved

// LibTest is running through a big Universe and a longer scenario
//

package com.zwsi.gblib

import org.junit.Test

import org.junit.Assert.*

class GBPlanetTest {

    @Test
    fun directions() {
        val universe = GBUniverse(2,2)
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

    }

}
