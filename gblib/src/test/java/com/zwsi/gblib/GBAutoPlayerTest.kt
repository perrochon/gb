package com.zwsi.gblib

import com.zwsi.gblib.GBController.Companion.u
import com.zwsi.gblib.GBData.Companion.BEETLES
import com.zwsi.gblib.GBData.Companion.GHOSTS
import com.zwsi.gblib.GBData.Companion.IMPI
import com.zwsi.gblib.GBData.Companion.TOOLS
import com.zwsi.gblib.GBData.Companion.TORTOISES
import com.zwsi.gblib.GBData.Companion.XENOS
import org.junit.Test

class GBAutoPlayerTest {

    @Test
    fun testXenos() {
        GBController.makeUniverse()
        GBAutoPlayer.playXenos(u.race(XENOS))
        for (i in 1..100) u.doUniverse()
    }

    @Test
    fun testImpi() {
        GBController.makeUniverse()
        GBAutoPlayer.playImpi(u.race(IMPI))
        for (i in 1..100) u.doUniverse()
    }


    @Test
    fun testBeetle() {
        GBController.makeUniverse()
        GBAutoPlayer.playBeetle(u.race(BEETLES))
        for (i in 1..100) u.doUniverse()
    }

    @Test
    fun testTortoise() {
        GBController.makeUniverse()
        GBAutoPlayer.playTortoise(u.race(TORTOISES))
        for (i in 1..100) u.doUniverse()
    }

    @Test
    fun testTools() {
        GBController.makeUniverse()
        GBAutoPlayer.playTools(u.race(TOOLS))
        for (i in 1..100) u.doUniverse()
    }

    @Test
    fun testGhosts() {
        GBController.makeUniverse()
        GBAutoPlayer.playGhosts(u.race(GHOSTS))
        for (i in 1..100) u.doUniverse()
    }

    @Test
    fun testTogether() {
        GBController.makeUniverse()
        GBAutoPlayer.playXenos(u.race(XENOS))
        GBAutoPlayer.playImpi(u.race(IMPI))
        GBAutoPlayer.playBeetle(u.race(BEETLES))
        GBAutoPlayer.playTortoise(u.race(TORTOISES))
        GBAutoPlayer.playTools(u.race(TOOLS))
        GBAutoPlayer.playGhosts(u.race(GHOSTS))
        for (i in 1..200) u.doUniverse()
    }

}