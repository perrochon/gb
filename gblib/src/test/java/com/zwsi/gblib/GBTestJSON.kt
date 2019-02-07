// Copyright 2018-2019 Louis Perrochon. All rights reserved

// GBTest tests saving to and restoring from JSON

package com.zwsi.gblib

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.zwsi.gblib.GBController.Companion.u
import org.junit.Test
import java.util.*

// Scratch class for testing JSON related stuff while developing. Finished tests go elsewhere
class GBTestJSON {

    @Test
    fun JSON() {

        val u = GBController.makeUniverse()
        val moshi = Moshi.Builder().build()

        val inObject = GBPlanet(u.getNextGlobalId(),0, 0, 0, GBLocation(1,0))
        val jsonAdapter1 = moshi.adapter<GBPlanet>(GBPlanet::class.java).indent("    ")
        val json1 = jsonAdapter1.toJson(inObject)
        val outObject = jsonAdapter1.fromJson(json1)

        assert(inObject == outObject)
    }

    @Test
    fun JSONMap() {

        val u = GBController.makeUniverse()
        val moshi = Moshi.Builder().build()

        val gameInfo1 = GBSavedGame("Starlist only", starList = u.allStars)
        val jsonAdapter3: JsonAdapter<GBSavedGame> = moshi.adapter(GBSavedGame::class.java).indent("    ")
        val json3 = jsonAdapter3.toJson(gameInfo1)
        val gameInfo2 = jsonAdapter3.lenient().fromJson(json3)

        assert(gameInfo1 == gameInfo2)
    }
}

