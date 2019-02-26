// Copyright 2018-2019 Louis Perrochon. All rights reserved

package com.zwsi.gblib

import com.zwsi.gblib.GBController.Companion.u
import org.junit.Test
import java.io.File


// This is not really a test. It creates missions for the game
// They need to manually be copied into the resources directory

class GBMakeMissions {


    @Test
    fun makeMission1() {
        GBController.makeSmallUniverse()
        val turns = 100
        var json: String = ""

        for (i in 1..turns) {
            json = GBController.doUniverse()
        }
        u.turn = 1
        u.description = "Mission 1"
        json = GBController.saveUniverse()
        File("levels/mission1.json").writeText(json)

        // Quick check we can load it again
        GBController.loadUniverse(json)
        GBController.doUniverse()

    }

    @Test
    fun makeMission2() {
        GBData.rand.nextInt()
        GBController.makeSmallUniverse()
        val turns = 200
        var json: String = ""

        for (i in 1..turns) {
            json = GBController.doUniverse()
        }
        u.turn = 1
        u.description = "Mission 2"
        json = GBController.saveUniverse()
        File("levels/mission2.json").writeText(json)

        // Quick check we can load it again
        GBController.loadUniverse(json)
        GBController.doUniverse()
    }

    @Test
    fun makeMission3() {
        GBData.rand.nextInt()
        GBData.rand.nextInt()
        GBController.makeUniverse()
        val turns = 300
        var json: String = ""

        for (i in 1..turns) {
            json = GBController.doUniverse()
        }
        u.turn = 1
        u.description = "Mission 3"
        json = GBController.saveUniverse()
        File("levels/mission3.json").writeText(json)

        // Quick check we can load it again
        GBController.loadUniverse(json)
        GBController.doUniverse()
    }

}

