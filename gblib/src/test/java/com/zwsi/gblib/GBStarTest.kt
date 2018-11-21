// Copyright 2018 Louis Perrochon. All rights reserved

// LibTest is running through a big Universe and a longer scenario
//

package com.zwsi.gblib

import org.junit.Test

import org.junit.Assert.*

class GBStarTest {

    fun consistent(s: GBStar){
        assert(s.name.length > 0)
        // assertEquals(s.uid, s.universe.allRaces.indexOf(s))
    }


    @Test
    fun basic() {
        val universe = GBUniverse(3,2)
        val s = universe.allStars[0]
        consistent(s)

    }


    @Test
    fun allConsitent() {
        val universe = GBUniverse(3,2)
        val s = universe.allStars[0]
        consistent(s)

    }

}
