// Copyright 2018 Louis Perrochon. All rights reserved

// LibTest is running through a big Universe and a longer scenario
//

package com.zwsi.gblib

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GBStarTest {

    fun consistent(s: GBStar) {
        val universe = GBController.universe

        assertTrue(s.name.length > 0)
        assertTrue(s.universe == universe)
        assertTrue(universe.allStars.contains(s))
        assertEquals(s.uid, universe.allStars.indexOf(s))
    }


    @Test
    fun basic() {
        val universe = GBController.makeUniverse()

        for (s in universe.allStars)
            consistent(s)

    }

}
