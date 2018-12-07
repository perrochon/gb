// Copyright 2018 Louis Perrochon. All rights reserved

// LibTest is running through a big Universe and a longer scenario
//

package com.zwsi.gblib

import org.junit.Test

import org.junit.Assert.*

class GBRaceTest {

    fun consistent(r: GBRace){
        val universe = GBController.universe

        assertTrue(r.description.length > 0)
        assertEquals(r.universe, universe)
        assertTrue(universe.allRaces.contains(r))
        assertEquals(r.uid, universe.allRaces.indexOf(r))
    }


    @Test
    fun basic() {
        val universe = GBController.makeUniverse()

        for (r in universe.allRaces) {
            consistent(r)
            assertEquals(r.getRaceShipsList().size,0 )
        }
    }
}
