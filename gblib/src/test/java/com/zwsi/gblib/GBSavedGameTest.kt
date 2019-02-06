// Copyright 2018 Louis Perrochon. All rights reserved

package com.zwsi.gblib

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.junit.Test
import java.io.File

class GBSavedGameTest {

    @Test
    fun JSONIfy() {
        val u = GBController.makeUniverse()
        val moshi = Moshi.Builder().build()

        val gameInfo1 = GBSavedGame("Test", u.allRaces, u.allStars, u.allPlanets)
        val jsonAdapter1: JsonAdapter<GBSavedGame> = moshi.adapter(GBSavedGame::class.java)
        val json = jsonAdapter1.toJson(gameInfo1)
        val gameInfo2 = jsonAdapter1.lenient().fromJson(json)
        assert(gameInfo1 == gameInfo2)
    }

    @Test
    fun JSONifyLong() {
        val u = GBController.makeUniverse()
        val moshi = Moshi.Builder().build()

        AutoPlayer.playBeetle()
        AutoPlayer.playImpi()
        AutoPlayer.playTortoise()

        for (i in 1..500) {
                GBController.doUniverse()
        }

        var description = "Number of Ships: ${u.allShips.size} | Ships Race 1: ${u.race(1).raceShipsUIDList.size}"
        val gameInfo1 = GBSavedGame(description, u.allRaces, u.allStars, u.allPlanets)
        val jsonAdapter1: JsonAdapter<GBSavedGame> = moshi.adapter(GBSavedGame::class.java).indent("  ")
        val json = jsonAdapter1.toJson(gameInfo1)
        File("testoutput/GBSavedGameTestJSONifyLong.json").writeText(json)
        val gameInfo2 = jsonAdapter1.lenient().fromJson(json)
        assert(gameInfo1 == gameInfo2)
    }

    @Test
    fun JSONIfyBig() {
        val u = GBController.makeBigUniverse()
        val moshi = Moshi.Builder().build()

        val gameInfo1 = GBSavedGame("Test", u.allRaces, u.allStars, u.allPlanets)
        val jsonAdapter1: JsonAdapter<GBSavedGame> = moshi.adapter(GBSavedGame::class.java)
        val json = jsonAdapter1.toJson(gameInfo1)
        val gameInfo2 = jsonAdapter1.lenient().fromJson(json)
        assert(gameInfo1 == gameInfo2)
    }

}