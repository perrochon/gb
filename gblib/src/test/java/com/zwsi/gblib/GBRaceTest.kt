// Copyright 2018 Louis Perrochon. All rights reserved

// LibTest is running through a big Universe and a longer scenario
//

package com.zwsi.gblib

import org.junit.Test

import org.junit.Assert.*

class GBRaceTest {

    fun consistent(r: GBRace){
        assert(r.description.length > 0)
        assertEquals(r.uid, r.universe.allRaces.indexOf(r))
    }


    @Test
    fun basic() {
        val universe = GBUniverse(3,2)
        val r = universe.allRaces[0]
    }

}
