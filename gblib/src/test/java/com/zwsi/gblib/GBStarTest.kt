// Copyright 2018-2019 Louis Perrochon. All rights reserved

// LibTest is running through a big Universe and a longer scenario
//

package com.zwsi.gblib

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.zwsi.gblib.GBController.Companion.u
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GBStarTest {

    fun consistent(s: GBStar) {
        val universe = GBController.u

        assertTrue(s.name.length > 0)
        assertTrue(u.stars.containsValue(s))
        assertTrue(u.stars.containsKey(s.uid))
        assertEquals(s.uid, u.star(s.uid).uid)
    }


    @Test
    fun basic() {
        GBController.makeUniverse()

        for ((_, s) in u.stars)
            consistent(s)

    }

    @Test
    fun JSON() {
        GBController.makeUniverse()
        val moshi = Moshi.Builder().build()
        val inObject = GBStar(u.getNextGlobalId(),12, 5, 100, 200)
        val jsonAdapter1 = moshi.adapter<GBStar>(GBStar::class.java)
        val json1 = jsonAdapter1.toJson(inObject)
        val outObject = jsonAdapter1.fromJson(json1)
        assert(inObject == outObject)
    }

//    @Test
//    fun JSONMap() {
//        GBController.makeUniverse()
//        val moshi = Moshi.Builder().build()
//
//        val gameInfo1 = GBSavedGame("Starlist Only", stars = u.stars)
//        val jsonAdapter1: JsonAdapter<GBSavedGame> = moshi.adapter(GBSavedGame::class.java)
//        val json = jsonAdapter1.toJson(gameInfo1)
//        val gameInfo2 = jsonAdapter1.lenient().fromJson(json)
//        assert(gameInfo1 == gameInfo2)
//    }
}
