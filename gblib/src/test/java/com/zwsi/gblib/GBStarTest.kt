// Copyright 2018 Louis Perrochon. All rights reserved

// LibTest is running through a big Universe and a longer scenario
//

package com.zwsi.gblib

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GBStarTest {

    fun consistent(s: GBStar) {
        val universe = GBController.u

        assertTrue(s.name.length > 0)
        assertTrue(universe.allStars.containsValue(s))
        assertTrue(universe.allStars.containsKey(s.uid))
        assertEquals(s.uid, universe.star(s.uid).uid)
    }


    @Test
    fun basic() {
        val universe = GBController.makeUniverse()

        for ((key, s) in universe.allStars)
            consistent(s)

    }

}
