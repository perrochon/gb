package com.zwsi.gblib

import com.zwsi.gblib.AutoPlayer
import com.zwsi.gblib.GBController.Companion.u
import org.junit.Test

class AutoPlayerTest {

    @Test
    fun testBeetle() {
        GBController.makeUniverse()
        AutoPlayer.playBeetle()
        for (i in 1..50) u.doUniverse()
    }

    @Test
    fun testTortoise() {
        GBController.makeUniverse()
        AutoPlayer.playTortoise()
        for (i in 1..50) u.doUniverse()
    }

    @Test
    fun testImpi() {
        GBController.makeUniverse()
        AutoPlayer.playImpi()
        for (i in 1..50) u.doUniverse()
    }

    @Test
    fun testTogether() {
        GBController.makeUniverse()
        AutoPlayer.playBeetle()
        AutoPlayer.playImpi()
        AutoPlayer.playTortoise()
        for (i in 1..200) u.doUniverse()
    }

}