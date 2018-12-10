// Copyright 2018 Louis Perrochon. All rights reserved

// LibTest is running through a big Universe and a longer scenario
//

package com.zwsi.gblib

import org.junit.Assert.assertEquals
import org.junit.Test

class GBSectorTest {

    @Test
    fun basicSector() {
        val universe = GBController.makeUniverse()
        val p = universe.allPlanets[0]
        val s = GBSector(p)
        assertEquals(s.planet, p)
    }
    //assertEquals(s.type_symbol,"?")
}
