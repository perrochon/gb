package com.zwsi.gblib

import com.zwsi.gblib.GBController.Companion.u
import org.junit.Test

class GBAutoPlayerTest {

    @Test
    fun noPlayers() {
        GBController.makeUniverse()
        u.autoPlayer.autoPlayers = arrayOf(false, false, false, false, false, false)
        for (i in 1..100) u.doUniverse()
    }

    @Test
    fun testXenos() {
        GBController.makeUniverse()
        u.autoPlayer.autoPlayers = arrayOf(true, false, false, false, false, false)
        for (i in 1..100) u.doUniverse()
    }

    @Test
    fun testImpi() {
        GBController.makeUniverse()
        u.autoPlayer.autoPlayers = arrayOf(false, true, false, false, false, false)
        for (i in 1..100) u.doUniverse()
    }


    @Test
    fun testBeetle() {
        GBController.makeUniverse()
        u.autoPlayer.autoPlayers = arrayOf(false, false, true, false, false, false)
        for (i in 1..100) u.doUniverse()
    }

    @Test
    fun testTortoise() {
        GBController.makeUniverse()
        u.autoPlayer.autoPlayers = arrayOf(false, false, false, true, false, false)
        for (i in 1..100) u.doUniverse()
    }

    @Test
    fun testTools() {
        GBController.makeUniverse()
        u.autoPlayer.autoPlayers = arrayOf(false, false, false, false, true, false)
        for (i in 1..100) u.doUniverse()
    }

    @Test
    fun testGhosts() {
        GBController.makeUniverse()
        u.autoPlayer.autoPlayers = arrayOf(false, false, false, false, false, true)
        for (i in 1..100) u.doUniverse()
    }

    @Test
    fun testTogether() {
        GBController.makeUniverse()
        for (i in 1..200) u.doUniverse()
    }

}