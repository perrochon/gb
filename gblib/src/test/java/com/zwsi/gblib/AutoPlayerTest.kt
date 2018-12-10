package com.zwsi.gblib

import com.zwsi.gb.gblib.AutoPlayer
import org.junit.Test

class AutoPlayerTest {

    @Test
    fun testBeetle() {
        val universe = GBController.makeUniverse()
        AutoPlayer.playBeetle()
        for (i in 1..200) universe.doUniverse()
    }

    @Test
    fun testTortoise() {
        val universe = GBController.makeUniverse()
        AutoPlayer.playTortoise()
        for (i in 1..200) universe.doUniverse()
    }

    @Test
    fun testImpi() {
        val universe = GBController.makeUniverse()
        AutoPlayer.playImpi()
        for (i in 1..200) universe.doUniverse()
    }

    @Test
    fun testTogether() {
        val universe = GBController.makeUniverse()
        AutoPlayer.playBeetle()
        AutoPlayer.playImpi()
        AutoPlayer.playTortoise()
        for (i in 1..200) universe.doUniverse()
    }

}