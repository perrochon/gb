// Copyright 2018 Louis Perrochon. All rights reserved
//
// // Location. Can be planet surface, planet orbit, system space, or deep space. All have different coordinates, etc.

package com.zwsi.gblib

import com.zwsi.gblib.GBDebug.gbAssert

class GBOrder(val universe: GBUniverse)  {

    var type = -1
    var uid = -1
    var uid2 = -1
    var x = -1
    var y = -1

    // Type 0: Make Factory
    fun makeFactory(planet: GBPlanet) {

        gbAssert{ type == -1 }
        type = 0
        uid = planet.uid
        x=0
        y=0

    }

    // Type 1: Make Pod
    fun makePod(ship: GBShip) {

        gbAssert{ type == -1 }
        type = 1
        uid = ship.uid
        x=0
        y=0

    }

    // Type 2: Fly Pod
    fun flyShip(sh: GBShip, planet: GBPlanet) {

        gbAssert{ type == -1 }
        type = 2
        uid = sh.uid
        uid2 = planet.uid
        x=0
        y=0

    }

    fun execute() {
        when (type) {
            0 -> {
                GBShip(0, this.universe.allRaces[0], 1, uid)
                universe.news.add("Built a factory on Helle.\n\n")
            }
            1 -> {
                GBShip(1, this.universe.allRaces[0], 1, uid)
                universe.news.add("Built a pod on Helle.\n\n")
            }
            2 -> {
                GBController.universe.allShips[uid].moveShip(1, uid2)
                universe.news.add("Moved ship to  " + GBController.universe.allPlanets[uid2].name + "\n\n")
            }
            else ->
                gbAssert ( "unknown oder", {true} )
        }

    }

}