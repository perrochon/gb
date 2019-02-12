// Copyright 2018-2019 Louis Perrochon. All rights reserved

package com.zwsi.gblib

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.zwsi.gblib.GBLocation.Companion.DEEPSPACE
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
        //File("testoutput/GBSavedGameTestInitialUniverse.json").writeText(json)
        val gameInfo2 = jsonAdapter1.lenient().fromJson(json)
        assert(gameInfo1 == gameInfo2)
    }

    @Test
    fun AgedUniverse() {
        val u = GBController.makeUniverse()
        val moshi = Moshi.Builder().build()
        val turns = 25

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
        //File("testoutput/GBSavedGameTestAgedUniverse$turns.json").writeText(json)
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
        val u = GBController.makeSmallUniverse()
        val moshi = Moshi.Builder().build()

        // First Turn
        u.makeFactory(u.planet(0), u.race(0))

        GBController.doUniverse()

        var info = "Universe After 1 turn"
        var gameInfo1 = GBSavedGame(info, u)
        File("testoutput/GBSavedGameTestPersistAndRestoreShips1.in.txt").writeText(
            gameInfo1.toString().replace(
                "GBStar",
                "\nGBStar", true
            ).replace(
                "GBPlanet",
                "\nGBPlanet", true
            ).replace(
                "GBShip",
                "\nGBShip", true
            ).replace(
                "GBRace",
                "\nGBRace", true
            )
        )
        val jsonAdapter: JsonAdapter<GBSavedGame> = moshi.adapter(GBSavedGame::class.java).indent("  ")
        var json = jsonAdapter.toJson(gameInfo1)
        File("testoutput/GBSavedGameTestPersistAndRestoreShips1.json").writeText(json)
        var gameInfo2 = jsonAdapter.lenient().fromJson(json)!!
        File("testoutput/GBSavedGameTestPersistAndRestoreShips1.out.txt").writeText(gameInfo2.toString())
        assert(gameInfo1 == gameInfo2)
        u.allStars = gameInfo2.starList!!
        u.allPlanets = gameInfo2.planetList!!
        u.allRaces = gameInfo2.raceList!!
        u.allShips = gameInfo2.shipList!!
        u.deepSpaceUidShips.clear()
        u.allShips.filterValues { it.loc.level == DEEPSPACE }.keys.forEach { u.deepSpaceUidShips.add(it) }

        //Second Turn
        val factory = u.allShips.filter { it.value.idxtype == GBData.FACTORY }.values.firstOrNull()!!
        u.makeCruiser(factory)

        GBController.doUniverse()

        info = "Universe After 2 turn"
        gameInfo1 = GBSavedGame(info, u)
        File("testoutput/GBSavedGameTestPersistAndRestoreShips2.in.txt").writeText(gameInfo1.toString())
        json = jsonAdapter.toJson(gameInfo1)
        File("testoutput/GBSavedGameTestPersistAndRestoreShips2.json").writeText(json)
        gameInfo2 = jsonAdapter.lenient().fromJson(json)!!
        File("testoutput/GBSavedGameTestPersistAndRestoreShips2.out.txt").writeText(gameInfo2.toString())
        assert(gameInfo1 == gameInfo2)
        u.allStars = gameInfo2.starList!!
        u.allPlanets = gameInfo2.planetList!!
        u.allRaces = gameInfo2.raceList!!
        u.allShips = gameInfo2.shipList!!
        u.deepSpaceUidShips.clear()
        u.allShips.filterValues { it.loc.level == DEEPSPACE }.keys.forEach { u.deepSpaceUidShips.add(it) }

        // Third Turn
        val cruiser = u.allShips.filter { it.value.idxtype == GBData.CRUISER }.values.firstOrNull()!!
        cruiser.dest = GBLocation(u.planet(1), 0f, 0f)
        GBController.doUniverse()

        info = "Universe After 3 turn"
        gameInfo1 = GBSavedGame(info, u)
        File("testoutput/GBSavedGameTestPersistAndRestoreShips3.in.txt").writeText(gameInfo1.toString())
        json = jsonAdapter.toJson(gameInfo1)
        File("testoutput/GBSavedGameTestPersistAndRestoreShips3.json").writeText(json)
        gameInfo2 = jsonAdapter.lenient().fromJson(json)!!
        File("testoutput/GBSavedGameTestPersistAndRestoreShips3.out.txt").writeText(gameInfo2.toString())
        assert(gameInfo1 == gameInfo2)
        u.allStars = gameInfo2.starList!!
        u.allPlanets = gameInfo2.planetList!!
        u.allRaces = gameInfo2.raceList!!
        u.allShips = gameInfo2.shipList!!
        u.deepSpaceUidShips.clear()
        u.allShips.filterValues { it.loc.level == DEEPSPACE }.keys.forEach { u.deepSpaceUidShips.add(it) }

    }

    @Test
    fun PersistAndRestoreShipsLong() {
        val u = GBController.makeUniverse()
        val moshi = Moshi.Builder().build()
        val turns = 5


        for (i in 1..turns) {
            GBController.doUniverse()

            val info = "Universe After $i turns"
            val gameInfo1 = GBSavedGame(info, shipList = u.allShips)

            val jsonAdapter1: JsonAdapter<GBSavedGame> = moshi.adapter(GBSavedGame::class.java).indent("  ")
            val json = jsonAdapter1.toJson(gameInfo1)
            val gameInfo2 = jsonAdapter1.lenient().fromJson(json)!!

            assert(gameInfo1 == gameInfo2)

            u.allShips = gameInfo2.shipList!!

            u.deepSpaceUidShips.clear()

            u.allShips.filterValues { it.loc.level == DEEPSPACE }.keys.forEach { u.deepSpaceUidShips.add(it) }

            u.deadShips.clear()
            // Not restoring any dead ships that we may have saved...

        }

    }

}