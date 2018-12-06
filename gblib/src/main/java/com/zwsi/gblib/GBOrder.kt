// Copyright 2018 Louis Perrochon. All rights reserved
//
// // Location. Can be planet surface, planet orbit, system space, or deep space. All have different coordinates, etc.

package com.zwsi.gblib

import com.zwsi.gblib.GBController.Companion.universe
import com.zwsi.gblib.GBData.Companion.CRUISER
import com.zwsi.gblib.GBData.Companion.FACTORY
import com.zwsi.gblib.GBData.Companion.POD
import com.zwsi.gblib.GBLog.gbAssert
import com.zwsi.gblib.GBLocation.Companion.LANDED


class GBOrder  {

    // TODO Lambdas? Or use the scheduler instead?

    var type = -1
    var uidShip = -1
    var uidRace = -1
    lateinit var loc : GBLocation

    // Type Factory
    fun makeFactory(loc: GBLocation, race: GBRace) {
        gbAssert{ type == -1 }
        type = FACTORY
        uidRace = race.uid
        gbAssert { loc.level == LANDED }
        this.loc = loc
    }

    // Type Pod
    fun makePod(factory: GBShip) {

        gbAssert{ type == -1 }
        type = POD
        uidShip = factory.uid
        uidRace = factory.race.uid
        this.loc = factory.loc

    }

    // Type Cruiser
    fun makeCruiser(factory: GBShip) {
        gbAssert{ type == -1 }
        type = CRUISER
        uidShip = factory.uid
        uidRace = factory.race.uid
        this.loc = factory.loc

    }

    fun execute() {
        when (type) {
            FACTORY -> {
                GBShip(FACTORY, universe.allRaces[uidRace], loc)
                universe.news.add("Built a factory on Helle.\n\n")
            }
            POD -> {
                GBShip(POD, universe.allRaces[uidRace], loc)
                universe.news.add("Built a pod on Helle.\n\n")
            }
            CRUISER -> {
                GBShip(CRUISER, universe.allRaces[uidRace], loc)
                universe.news.add("Built a cruiser on Helle.\n\n")
            }
            else ->
                gbAssert ( "unknown oder", {true} )
        }

    }

}