// Copyright 2018-2019 Louis Perrochon. All rights reserved

package com.zwsi.gblib

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.junit.Test
import java.io.File

class GBSavedGameTest {

    // TODO TEST PERF reduce number of moshi's and adapters built...

    @Test
    fun InitialUniverse() {
        val u = GBController.makeUniverse()
        val moshi = Moshi.Builder().build()

        val info = "Initial Universe: Number of Ships: ${u.allShips.size}"
        val gameInfo1 = GBSavedGame(info, u)
        val jsonAdapter1: JsonAdapter<GBSavedGame> = moshi.adapter(GBSavedGame::class.java).indent("  ")
        val json = jsonAdapter1.toJson(gameInfo1)
        File("testoutput/GBSavedGameTestInitialUniverse.json").writeText(json)
        val gameInfo2 = jsonAdapter1.lenient().fromJson(json)
        assert(gameInfo1 == gameInfo2)
    }

    @Test
    fun AgedUniverse() {
        val u = GBController.makeUniverse()
        val moshi = Moshi.Builder().build()
        val turns = 50

        AutoPlayer.playBeetle()
        AutoPlayer.playImpi()
        AutoPlayer.playTortoise()

        for (i in 1..turns) {
            GBController.doUniverse()
        }

        val info = "Universe After $turns turns: Number of Ships: ${u.allShips.size}"
        val gameInfo1 = GBSavedGame(info, u)
        val jsonAdapter1: JsonAdapter<GBSavedGame> = moshi.adapter(GBSavedGame::class.java).indent("  ")
        val json = jsonAdapter1.toJson(gameInfo1)
        File("testoutput/GBSavedGameTestAgedUniverse$turns.json").writeText(json)
        val gameInfo2 = jsonAdapter1.lenient().fromJson(json)

        //File("testoutput/GBSavedGameTestAgedUniverse$turns.in.txt").writeText(gameInfo1.toString())
        //File("testoutput/GBSavedGameTestAgedUniverse$turns.out.txt").writeText(gameInfo2.toString())

        assert(gameInfo1 == gameInfo2)
    }

    @Test
    fun InitialBigUniverse() {
        val u = GBController.makeBigUniverse()
        val moshi = Moshi.Builder().build()

        val info = "Initial Big Universe "
        val gameInfo1 = GBSavedGame(info, u)
        val jsonAdapter1: JsonAdapter<GBSavedGame> = moshi.adapter(GBSavedGame::class.java)
        val json = jsonAdapter1.toJson(gameInfo1)
        val gameInfo2 = jsonAdapter1.lenient().fromJson(json)
        assert(gameInfo1 == gameInfo2)
    }

    @Test
    fun PersistAndRestoreStarsPlanetsRaces() {
        val u = GBController.makeUniverse()
        val moshi = Moshi.Builder().build()
        val turns = 10

        for (i in 1..turns) {
            GBController.doUniverse()

            val info = "Universe After $i turns"
            val gameInfo1 = GBSavedGame(info, u)
            val jsonAdapter1: JsonAdapter<GBSavedGame> = moshi.adapter(GBSavedGame::class.java).indent("  ")
            val json = jsonAdapter1.toJson(gameInfo1)
            //File("testoutput/GBSavedGameTestPersistAndRestoreSPR$i.json").writeText(json)
            val gameInfo2 = jsonAdapter1.lenient().fromJson(json)!!
            (gameInfo1.toString())

            assert(gameInfo1 == gameInfo2)

            u.allStars = gameInfo2.starList!!
            u.allPlanets = gameInfo2.planetList!!
            u.allRaces = gameInfo2.raceList!!

        }

    }

    @Test
    fun PersistAndRestoreShips() {
        val u = GBController.makeUniverse()
        val moshi = Moshi.Builder().build()
        val turns = 5


        for (i in 1..turns) {
            AutoPlayer.playBeetle()
            GBController.doUniverse()

            val info = "Universe After $i turns"
            val gameInfo1 = GBSavedGame(info, shipList = u.allShips)
            val jsonAdapter1: JsonAdapter<GBSavedGame> = moshi.adapter(GBSavedGame::class.java).indent("  ")
            val json = jsonAdapter1.toJson(gameInfo1)
            File("testoutput/GBSavedGameTestPersistAndRestoreShips$i.json").writeText(json)
            val gameInfo2 = jsonAdapter1.lenient().fromJson(json)!!
            assert(gameInfo1 == gameInfo2)

            u.allShips = gameInfo2.shipList!!

            u.deepSpaceShips.clear()

            // FIXME Re-create DeepSpaceShip

            u.deadShips.clear()
            // Not restoring any dead ships that we may have saved...

            GBScheduler.scheduledActions.clear()

            // FIXME -> Need Autoplayer to be more robust

        }

    }

}