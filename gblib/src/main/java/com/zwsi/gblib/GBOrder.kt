// Copyright 2018 Louis Perrochon. All rights reserved
//
// // Location. Can be planet surface, planet orbit, system space, or deep space. All have different coordinates, etc.

package com.zwsi.gblib

import com.zwsi.gblib.GBController.Companion.universe
import com.zwsi.gblib.GBDebug.gbAssert
import com.zwsi.gblib.GBLocation.Companion.LANDED


class GBOrder  {

    var type = -1
    var uid = -1
    lateinit var loc : GBLocation

    // Type 0: Make Factory
    fun makeFactory(loc: GBLocation) {

        gbAssert{ type == -1 }
        type = 0
        gbAssert { loc.level == LANDED }
        this.loc = loc
    }

    // Type 1: Make Pod
    fun makePod(factory: GBShip) {

        gbAssert{ type == -1 }
        type = 1
        uid = factory.uid
        this.loc = factory.loc

    }

    // Type 2: Teleport ship
    fun teleportShip(sh: GBShip, loc: GBLocation) {

        gbAssert{ type == -1 }
        type = 2
        uid = sh.uid
        this.loc = loc // TODO Find a better sector, but not here, in the caller

    }

    fun execute() {
        when (type) {
            0 -> {
                GBShip(0, universe.allRaces[0], loc)
                universe.news.add("Built a factory on Helle.\n\n")
            }
            1 -> {
                GBShip(1, universe.allRaces[0], loc)
                universe.news.add("Built a pod on Helle.\n\n")
            }
            2 -> {
                GBController.universe.allShips[uid].moveShip(loc)
                universe.news.add("Moved ship to  " + loc.getLocDesc() + "\n\n")
            }
            else ->
                gbAssert ( "unknown oder", {true} )
        }

    }

}