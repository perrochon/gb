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
        assertTrue(universe.allRaces.containsValue(r))
        //assertEquals(r.uid, universe.allRaces.indexOf(r))
    }


    @Test
    fun basic() {
        val universe = GBController.makeUniverse()

        for ((key,race) in universe.allRaces) {
            consistent(race)
            assertEquals(race.getRaceShipsList().size,0 )
        }

        assert(universe.race(0) == universe.race(0))
        assert(universe.race(0) != universe.race(1))

        // TODO TEST add ships to class

        // Testing Data Class Behavior
        val copy1 = universe.race(1)
        assert(universe.race(1) == copy1)
        assert(universe.race(1) === copy1) // Pointing to the same class

        val copy2 = universe.race(1).copy()
        assert(universe.race(1) == copy2)
        assert(universe.race(1) !== copy2) // Pointing to different class

    }
}
