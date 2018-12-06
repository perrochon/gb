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
    var uid = -1
    lateinit var loc : GBLocation

    // Type Factory
    fun makeFactory(loc: GBLocation) {

        gbAssert{ type == -1 }
        type = FACTORY
        gbAssert { loc.level == LANDED }
        this.loc = loc
    }

    // Type Pod
    fun makePod(factory: GBShip) {

        gbAssert{ type == -1 }
        type = POD
        uid = factory.uid
        this.loc = factory.loc

    }

    // Type Cruiser
    fun makeCruiser(factory: GBShip) {

        gbAssert{ type == -1 }
        type = CRUISER
        uid = factory.uid
        this.loc = factory.loc

    }

    fun execute() {
        when (type) {
            FACTORY -> {
                GBShip(FACTORY, universe.allRaces[0], loc)
                universe.news.add("Built a factory on Helle.\n\n")
            }
            POD -> {
                GBShip(POD, universe.allRaces[0], loc)
                universe.news.add("Built a pod on Helle.\n\n")
            }
            CRUISER -> {
                GBShip(CRUISER, universe.allRaces[0], loc)
                universe.news.add("Built a cruiser on Helle.\n\n")
            }
            else ->
                gbAssert ( "unknown oder", {true} )
        }

    }

}