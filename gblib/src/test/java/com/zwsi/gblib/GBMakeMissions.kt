// Copyright 2018-2019 Louis Perrochon. All rights reserved

package com.zwsi.gblib

import com.zwsi.gblib.GBController.Companion.u
import com.zwsi.gblib.GBData.Companion.Demo1
import com.zwsi.gblib.GBData.Companion.FACTORY
import com.zwsi.gblib.GBData.Companion.Map1
import com.zwsi.gblib.GBData.Companion.Map2
import com.zwsi.gblib.GBData.Companion.Map3
import com.zwsi.gblib.GBData.Companion.Mission1
import com.zwsi.gblib.GBData.Companion.Mission2
import com.zwsi.gblib.GBData.Companion.Mission3
import com.zwsi.gblib.GBData.Companion.Mission4
import com.zwsi.gblib.GBData.Companion.Mission5
import com.zwsi.gblib.GBData.Companion.Mission6
import org.junit.Test
import java.io.File


// This is not really a test. It creates missions for the game
// They need to manually be copied into the resources directory
// Missions: Single Player
// Maps: Two Players

class GBMakeMissions {

    @Test
    fun makeMission1() {
        GBController.makeUniverse(1, 1)
        u.star(0).loc = GBLocation(410f, 710f)
        u.id = Mission1
        u.description = "Mission 1: Expand through your solar system"
        val json = GBController.saveUniverse()
        File("levels/mission1.json").writeText(json)

        // Quick check we can load it again
        GBController.loadUniverseFromJSON(json)
        u.doUniverse()

    }

    @Test
    fun makeMission2() {
        GBController.makeUniverse(5, 2)
        u.star(0).loc = GBLocation(310f, 610f)
        u.star(1).loc = GBLocation(490f, 790f)
        u.star(2).loc = GBLocation(250f, 850f)
        u.star(3).loc = GBLocation(480f, 620f)
        u.star(4).loc = GBLocation(400f, 700f)

        u.id = Mission2
        u.description = "Mission 2: Conquer neighbouring systems"
        val json = GBController.saveUniverse()
        File("levels/mission2.json").writeText(json)

        // Quick check we can load it again
        GBController.loadUniverseFromJSON(json)
        u.doUniverse()
    }

    @Test
    fun makeMission3() {
        GBController.makeUniverse(5, 3)
        u.star(0).loc = GBLocation(310f, 610f)
        u.star(1).loc = GBLocation(490f, 790f)
        u.star(2).loc = GBLocation(250f, 850f)
        u.star(3).loc = GBLocation(480f, 620f)
        u.star(4).loc = GBLocation(400f, 700f)

        u.id = Mission3
        u.description = "Mission 3: Conquer neighbouring systems"
        val json = GBController.saveUniverse()
        File("levels/mission3.json").writeText(json)

        // Quick check we can load it again
        GBController.loadUniverseFromJSON(json)
        u.doUniverse()
    }

    @Test
    fun makeMission4() {
        GBController.makeUniverse(5, 4)
        u.star(0).loc = GBLocation(310f, 610f)
        u.star(1).loc = GBLocation(490f, 790f)
        u.star(2).loc = GBLocation(250f, 850f)
        u.star(3).loc = GBLocation(480f, 620f)
        u.star(4).loc = GBLocation(400f, 700f)

        u.id = Mission4
        u.description = "Mission 4: Conquer neighbouring systems"
        val json = GBController.saveUniverse()
        File("levels/mission4.json").writeText(json)

        // Quick check we can load it again
        GBController.loadUniverseFromJSON(json)
        u.doUniverse()
    }

    @Test
    fun makeMission5() {
        GBController.makeUniverse(5, 5)
        u.star(0).loc = GBLocation(310f, 610f)
        u.star(1).loc = GBLocation(490f, 790f)
        u.star(2).loc = GBLocation(250f, 850f)
        u.star(3).loc = GBLocation(480f, 620f)
        u.star(4).loc = GBLocation(400f, 700f)

        u.id = Mission5
        u.description = "Mission 5: Conquer neighbouring systems"
        val json = GBController.saveUniverse()
        File("levels/mission5.json").writeText(json)

        // Quick check we can load it again
        GBController.loadUniverseFromJSON(json)
        u.doUniverse()
    }

    @Test
    fun makeMission6() {
        GBController.makeUniverse(6, 6)
        u.star(0).loc = GBLocation(310f, 610f)
        u.star(1).loc = GBLocation(490f, 790f)
        u.star(2).loc = GBLocation(250f, 850f)
        u.star(3).loc = GBLocation(480f, 620f)
        u.star(4).loc = GBLocation(400f, 700f)
        u.star(5).loc = GBLocation(610f, 490f)

        u.id = Mission6
        u.description = "Mission 6: Conquer neighbouring systems"
        val json = GBController.saveUniverse()
        File("levels/mission6.json").writeText(json)

        // Quick check we can load it again
        GBController.loadUniverseFromJSON(json)
        u.doUniverse()
    }

    @Test
    fun makeMap1() {
        GBController.makeUniverse(5, 2)
        u.star(0).loc = GBLocation(310f, 610f)
        u.star(1).loc = GBLocation(490f, 790f)
        u.star(2).loc = GBLocation(250f, 850f)
        u.star(3).loc = GBLocation(480f, 620f)
        u.star(4).loc = GBLocation(350f, 750f)

        GBController.makeStructure(u.race(0).uidHomePlanet, 0, FACTORY)
        GBController.makeStructure(u.race(1).uidHomePlanet, 1, FACTORY)

        val turns = 10
        for (i in 1..turns) {
            u.doUniverse()
        }
        u.turn = 1
        u.id = Map1
        u.description = "Map 1: Virgo"
        u.secondPlayer = true
        u.race(0).money = 500
        u.race(1).money = 500
        val json = GBController.saveUniverse()
        File("levels/map1.json").writeText(json)

        // Quick check we can load it again
        GBController.loadUniverseFromJSON(json)
        u.doUniverse()
    }

    @Test
    fun makeMap2() {
        GBController.makeUniverse(6, 6)
        u.star(1).loc = GBLocation(310f, 610f)
        u.star(0).loc = GBLocation(490f, 790f)
        u.star(4).loc = GBLocation(300f, 800f)
        u.star(3).loc = GBLocation(480f, 620f)
        u.star(2).loc = GBLocation(400f, 700f)
        u.star(5).loc = GBLocation(610f, 490f)

        GBController.makeStructure(u.race(0).uidHomePlanet, 0, FACTORY)
        GBController.makeStructure(u.race(1).uidHomePlanet, 1, FACTORY)

        val turns = 30
        for (i in 1..turns) {
            u.doUniverse()
        }
        u.turn = 1
        u.id = Map2
        u.description = "Map 2: Southern Cross"
        u.secondPlayer = true
        u.race(0).money = 1000
        u.race(1).money = 1000
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
        u.id = Map3
        u.description = "Map 3: All out War"
        u.secondPlayer = true
        u.race(0).money = 1000
        u.race(1).money = 1000
        val json = GBController.saveUniverse()
        File("levels/map3.json").writeText(json)

        // Quick check we can load it again
        GBController.loadUniverseFromJSON(json)
        u.doUniverse()
    }

    @Test
    fun makeDemo1() {
        GBController.makeUniverse(6, 6)
        u.star(0).loc = GBLocation(284f,446f)
        u.star(1).loc = GBLocation(195f, 400f)
        u.star(2).loc = GBLocation(111f, 455f)
        u.star(3).loc = GBLocation(116f, 554f)
        u.star(4).loc = GBLocation(205f, 600f)
        u.star(5).loc = GBLocation(289f, 545f)

        u.id = Demo1
        u.description = "Demo 1: The Hexagon of Death"
        u.demoMode = true
        val json = GBController.saveUniverse()
        File("levels/demo1.json").writeText(json)

        // Quick check we can load it again
        GBController.loadUniverseFromJSON(json)
        u.doUniverse()
    }


}

