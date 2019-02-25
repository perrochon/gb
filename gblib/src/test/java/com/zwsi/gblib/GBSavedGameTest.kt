// Copyright 2018-2019 Louis Perrochon. All rights reserved

package com.zwsi.gblib

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.zwsi.gblib.GBController.Companion.u
import com.zwsi.gblib.GBLocation.Companion.DEEPSPACE
import org.junit.Test
import java.io.File

class GBSavedGameTest {

    fun compareUniverses(before: GBUniverse, after: GBUniverse) {
    }


    @Test
    fun InitialUniverse() {
        GBController.makeUniverse()
        val moshi = Moshi.Builder().build()

        val info = "Initial Universe: Number of Ships: ${u.ships.size}"
        val gameInfo1 = GBSavedGame(info, u)
        val jsonAdapter1: JsonAdapter<GBSavedGame> = moshi.adapter(GBSavedGame::class.java).indent("  ")
        val json = jsonAdapter1.toJson(gameInfo1)
        File("testoutput/GBSavedGameTestInitialUniverse.json").writeText(json)
        val gameInfo2 = jsonAdapter1.lenient().fromJson(json)
        assert(gameInfo1 == gameInfo2)
    }

    @Test
    fun InitialUniverseSaveAndLoad() {
        GBController.makeUniverse()
        val before = GBController.u
        GBController.saveUniverse()
        GBController.loadUniverse()
        val after = GBController.u
        compareUniverses(before, after)
    }


    @Test
    fun AgedUniverse() {
        GBController.makeUniverse()
        val moshi = Moshi.Builder().build()
        val turns = 25 // Don't change this or other tests break

        for (i in 1..turns) {
            GBController.doUniverse()
        }

        val info = "Universe After $turns turns: Number of Ships: ${u.ships.size}"
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
    fun AgedUniverseSaveAndLoad() {
        val turns = 25
        GBController.makeUniverse()

        for (i in 1..turns) {
            GBController.doUniverse()
            GBController.saveUniverse()
            GBController.loadUniverse()
        }
    }

    @Test
    fun loadInitialFromJsonAfterMakeUniverse() {
        GBController.makeUniverse() // Here we make a Universe. Wasteful, should not be needed
        val json = File("testoutput/GBSavedGameTestInitialUniverse.json").readText()
        GBController.loadUniverse(json)
        GBController.doUniverse()
    }

    @Test
    fun loadInitialFromJason() {
        val json = File("testoutput/GBSavedGameTestInitialUniverse.json").readText()
        GBController.loadUniverse(json)
        GBController.doUniverse()
    }

    @Test
    fun loadAged25FromJsonAfterMakeUniverse() {
        GBController.makeUniverse() // Here we make a Universe. Wasteful, should not be needed
        val json = File("testoutput/GBSavedGameTestAgedUniverse25.json").readText()
        GBController.loadUniverse(json)
        GBController.doUniverse()
    }

    @Test
    fun loadAged25FromJason() {
        val json = File("testoutput/GBSavedGameTestAgedUniverse25.json").readText()
        GBController.loadUniverse(json)
        GBController.doUniverse()
    }

    @Test
    fun loadCurrentFromJason() {
        val json = File("CurrentGame.json").readText()
        GBController.loadUniverse(json)
        GBController.doUniverse()
    }


    @Test
    fun InitialBigUniverse() {
        GBController.makeBigUniverse()
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
        GBController.makeUniverse()
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

            u.stars = gameInfo2.stars!!
            u.planets = gameInfo2.planets!!
            u.races = gameInfo2.races!!

        }

    }

    @Test
    fun PersistAndRestoreShips() {
        GBController.makeSmallUniverse()
        val moshi = Moshi.Builder().build()

        // First Turn
        GBController.makeFactory(0, 0)

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
        u.stars = gameInfo2.stars!!
        u.planets = gameInfo2.planets!!
        u.races = gameInfo2.races!!
        u.ships = gameInfo2.ships!!
        u.deepSpaceUidShips.clear()
        u.ships.filterValues { it.loc.level == DEEPSPACE }.keys.forEach { u.deepSpaceUidShips.add(it) }

        //Second Turn
        val factory = u.ships.filter { it.value.idxtype == GBData.FACTORY }.values.firstOrNull()!!
        GBController.makeCruiser(factory.uid)

        GBController.doUniverse()

        info = "Universe After 2 turn"
        gameInfo1 = GBSavedGame(info, u)
        File("testoutput/GBSavedGameTestPersistAndRestoreShips2.in.txt").writeText(gameInfo1.toString())
        json = jsonAdapter.toJson(gameInfo1)
        File("testoutput/GBSavedGameTestPersistAndRestoreShips2.json").writeText(json)
        gameInfo2 = jsonAdapter.lenient().fromJson(json)!!
        File("testoutput/GBSavedGameTestPersistAndRestoreShips2.out.txt").writeText(gameInfo2.toString())
        assert(gameInfo1 == gameInfo2)
        u.stars = gameInfo2.stars!!
        u.planets = gameInfo2.planets!!
        u.races = gameInfo2.races!!
        u.ships = gameInfo2.ships!!
        u.deepSpaceUidShips.clear()
        u.ships.filterValues { it.loc.level == DEEPSPACE }.keys.forEach { u.deepSpaceUidShips.add(it) }

        // Third Turn
        val cruiser = u.ships.filter { it.value.idxtype == GBData.CRUISER }.values.firstOrNull()!!
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
        u.stars = gameInfo2.stars!!
        u.planets = gameInfo2.planets!!
        u.races = gameInfo2.races!!
        u.ships = gameInfo2.ships!!
        u.deepSpaceUidShips.clear()
        u.ships.filterValues { it.loc.level == DEEPSPACE }.keys.forEach { u.deepSpaceUidShips.add(it) }

    }

    @Test
    fun PersistAndRestoreShipsLong() {
        GBController.makeUniverse()
        val moshi = Moshi.Builder().build()
        val turns = 5


        for (i in 1..turns) {
            GBController.doUniverse()

            val info = "Universe After $i turns"
            val gameInfo1 = GBSavedGame(info, ships = u.ships)

            val jsonAdapter1: JsonAdapter<GBSavedGame> = moshi.adapter(GBSavedGame::class.java).indent("  ")
            val json = jsonAdapter1.toJson(gameInfo1)
            val gameInfo2 = jsonAdapter1.lenient().fromJson(json)!!

            assert(gameInfo1 == gameInfo2)

            u.ships = gameInfo2.ships!!

            u.deepSpaceUidShips.clear()

            u.ships.filterValues { it.loc.level == DEEPSPACE }.keys.forEach { u.deepSpaceUidShips.add(it) }

            u.deadShips.clear()
            // Not restoring any dead ships that we may have saved...

        }

    }

}