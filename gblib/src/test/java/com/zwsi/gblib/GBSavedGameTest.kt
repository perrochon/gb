// Copyright 2018 Louis Perrochon. All rights reserved

package com.zwsi.gblib

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.junit.Test

class GBSavedGameTest {

    @Test
    fun JSONIfy() {
        val u = GBController.makeUniverse()
        val moshi = Moshi.Builder().build()

        val gameInfo1 = GBSavedGame(u.allRaces, u.allStars, u.allPlanets)
        val jsonAdapter1: JsonAdapter<GBSavedGame> = moshi.adapter(GBSavedGame::class.java)
        val json = jsonAdapter1.toJson(gameInfo1)
        val gameInfo2 = jsonAdapter1.lenient().fromJson(json)
        assert(gameInfo1 == gameInfo2)
    }

}