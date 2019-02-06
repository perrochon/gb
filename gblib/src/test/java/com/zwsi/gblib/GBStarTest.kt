// Copyright 2018 Louis Perrochon. All rights reserved

// LibTest is running through a big Universe and a longer scenario
//

package com.zwsi.gblib

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
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

    @Test
    fun JSON() {
        val u = GBController.makeUniverse()
        val moshi = Moshi.Builder().build()
        val inObject = GBStar(u.getNextGlobalId(),12, 5, 100, 200)
        val jsonAdapter1 = moshi.adapter<GBStar>(GBStar::class.java)
        val json1 = jsonAdapter1.toJson(inObject)
        val outObject = jsonAdapter1.fromJson(json1)
        assert(inObject == outObject)
    }

    @Test
    fun JSONMap() {
        val u = GBController.makeUniverse()
        val moshi = Moshi.Builder().build()

        val gameInfo1 = GBSavedGame("Test", null, u.allStars, null)
        val jsonAdapter1: JsonAdapter<GBSavedGame> = moshi.adapter(GBSavedGame::class.java)
        val json = jsonAdapter1.toJson(gameInfo1)
        val gameInfo2 = jsonAdapter1.lenient().fromJson(json)
        assert(gameInfo1 == gameInfo2)

    }
}
