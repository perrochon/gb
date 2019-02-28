package com.zwsi.gblib

import com.zwsi.gblib.GBController.Companion.u
import org.junit.Test

class GBAutoPlayerTest {

    @Test
    fun testBeetle() {
        GBController.makeUniverse()
        GBAutoPlayer.playBeetle()
        for (i in 1..50) u.doUniverse()
    }

    @Test
    fun testTortoise() {
        GBController.makeUniverse()
        GBAutoPlayer.playTortoise()
        for (i in 1..50) u.doUniverse()
    }

    @Test
    fun testImpi() {
        GBController.makeUniverse()
        GBAutoPlayer.playImpi()
        for (i in 1..50) u.doUniverse()
    }

    @Test
    fun testTogether() {
        GBController.makeUniverse()
        GBAutoPlayer.playBeetle()
        GBAutoPlayer.playImpi()
        GBAutoPlayer.playTortoise()
        for (i in 1..200) u.doUniverse()
    }

}