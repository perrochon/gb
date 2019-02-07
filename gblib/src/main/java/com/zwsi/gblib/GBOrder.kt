// Copyright 2018 Louis Perrochon. All rights reserved
//
// // Location. Can be planet surface, planet orbit, system space, or deep space. All have different coordinates, etc.

package com.zwsi.gblib

import com.zwsi.gblib.GBController.Companion.u
import com.zwsi.gblib.GBData.Companion.CRUISER
import com.zwsi.gblib.GBData.Companion.FACTORY
import com.zwsi.gblib.GBData.Companion.POD
import com.zwsi.gblib.GBData.Companion.rand
import com.zwsi.gblib.GBLog.gbAssert
import com.zwsi.gblib.GBLocation.Companion.LANDED

// FIXME PERSISTENCE. Pending orders need to be persistent, too. Maybe easier to make them GBInstructions as those need
// to be persisted...


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

    // TODO check for nulls and correct types, but this can wait until we made ships a hierarchy and repalced orders with schedule

    // Type Pod
    fun makePod(factory: GBShip) {

        if (factory.health > 0) {
            gbAssert { type == -1 }
            type = POD
            uidShip = factory.uid
            uidRace = factory.uidRace
            this.loc = GBLocation(
                factory.loc.getPlanet()!!,
                rand.nextInt(factory.loc.getPlanet()!!.width),
                rand.nextInt(factory.loc.getPlanet()!!.height)
            )
        }
    }

    // Type Cruiser
    fun makeCruiser(factory: GBShip) {
        if (factory.health > 0) {
            gbAssert { type == -1 }
            type = CRUISER
            uidShip = factory.uid
            uidRace = factory.uidRace
            this.loc = GBLocation(
                factory.loc.getPlanet()!!,
                rand.nextInt(factory.loc.getPlanet()!!.width),
                rand.nextInt(factory.loc.getPlanet()!!.height)
            )
        }
    }

    fun execute() {
        when (type) {
            FACTORY -> {
                GBShip(u.getNextGlobalId(), FACTORY, uidRace, loc)
                u.news.add("Built a factory on Helle.\n\n")
            }
            POD -> {
                GBShip(u.getNextGlobalId(), POD, uidRace, loc)
                u.news.add("Built a pod on Helle.\n\n")
            }
            CRUISER -> {
                GBShip(u.getNextGlobalId(), CRUISER, uidRace, loc)
                u.news.add("Built a cruiser on Helle.\n\n")
            }
            else ->
                gbAssert ( "unknown order", {true} )
        }

    }

}