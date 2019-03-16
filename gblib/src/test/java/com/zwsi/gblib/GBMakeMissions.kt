// Copyright 2018-2019 Louis Perrochon. All rights reserved

package com.zwsi.gblib

import com.zwsi.gblib.GBController.Companion.u
import com.zwsi.gblib.GBData.Companion.FACTORY
import org.junit.Test
import java.io.File


// This is not really a test. It creates missions for the game
// They need to manually be copied into the resources directory
// Missions: Single Player
// Maps: Two Players

class GBMakeMissions {

    @Test
    fun makeMission1() {
        GBController.makeUniverse(1,1)
        u.star(0).loc = GBLocation(410f,710f)
        u.description = "Mission 1: Expand through your solar system"
        val json = GBController.saveUniverse()
        File("levels/mission1.json").writeText(json)

        // Quick check we can load it again
        GBController.loadUniverseFromJSON(json)
        u.doUniverse()

    }

    @Test
    fun makeMission2() {
        GBController.makeUniverse(5,4)
        u.star(0).loc = GBLocation(310f,610f)
        u.star(1).loc = GBLocation(490f,790f)
        u.star(2).loc = GBLocation(250f,850f)
        u.star(3).loc = GBLocation(480f,620f)
        u.star(4).loc = GBLocation(400f,700f)

        u.description = "Mission 2: Conquer neighbouring systems"
        val json = GBController.saveUniverse()
        File("levels/mission2.json").writeText(json)

        // Quick check we can load it again
        GBController.loadUniverseFromJSON(json)
        u.doUniverse()
    }

    @Test
    fun makeMission3() {
        GBController.makeUniverse()
        val turns = 300
        for (i in 1..turns) {
            u.doUniverse()
        }
        u.turn = 1
        u.description = "Mission 3: Good luck!"
        val json = GBController.saveUniverse()
        File("levels/mission3.json").writeText(json)

        // Quick check we can load it again
        GBController.loadUniverseFromJSON(json)
        u.doUniverse()
    }

    @Test
    fun makeMap1() {
        GBController.makeUniverse(5,4)
        u.star(0).loc = GBLocation(310f,610f)
        u.star(1).loc = GBLocation(490f,790f)
        u.star(2).loc = GBLocation(250f,850f)
        u.star(3).loc = GBLocation(480f,620f)
        u.star(4).loc = GBLocation(350f,750f)

        GBController.makeStructure(u.race(0).uidHomePlanet, 0, FACTORY)
        GBController.makeStructure(u.race(1).uidHomePlanet, 1, FACTORY)

        val turns = 10
        for (i in 1..turns) {
            u.doUniverse()
        }
        u.turn = 1
        u.description = "Map 1: Virgo"
        u.secondPlayer = true
        val json = GBController.saveUniverse()
        File("levels/map1.json").writeText(json)

        // Quick check we can load it again
        GBController.loadUniverseFromJSON(json)
        u.doUniverse()

    }

    @Test
    fun makeMap2() {
        GBController.makeUniverse(5,4)
        u.star(1).loc = GBLocation(310f,610f)
        u.star(0).loc = GBLocation(490f,790f)
        u.star(4).loc = GBLocation(300f,800f)
        u.star(3).loc = GBLocation(480f,620f)
        u.star(2).loc = GBLocation(400f,700f)

        GBController.makeStructure(u.race(0).uidHomePlanet, 0, FACTORY)
        GBController.makeStructure(u.race(1).uidHomePlanet, 1, FACTORY)

        val turns = 30
        for (i in 1..turns) {
            u.doUniverse()
        }
        u.turn = 1
        u.description = "Map 2: Southern Cross"
        u.secondPlayer = true
        val json = GBController.saveUniverse()
        File("levels/map2.json").writeText(json)

        // Quick check we can load it again
        GBController.loadUniverseFromJSON(json)
        u.doUniverse()

    }

    @Test
    fun makeMap3() {
        GBData.rand.nextInt()
        GBData.rand.nextInt()
        GBController.makeUniverse()
        val turns = 300

        for (i in 1..turns) {
            u.doUniverse()
        }
        u.turn = 1
        u.description = "Map 3: All out War"
        u.secondPlayer = true
        val json = GBController.saveUniverse()
        File("levels/map3.json").writeText(json)

        // Quick check we can load it again
        GBController.loadUniverseFromJSON(json)
        u.doUniverse()
    }

}

