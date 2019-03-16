// Copyright 2018-2019 Louis Perrochon. All rights reserved
//
// // Location. Can be planet surface, planet orbit, system space, or deep space. All have different coordinates, etc.

package com.zwsi.gblib

import com.zwsi.gblib.GBController.Companion.u
import com.zwsi.gblib.GBData.Companion.FACTORY
import com.zwsi.gblib.GBLog.gbAssert
import kotlin.math.PI


class GBOrder {
    var type = -1
    var uidShip = -1
    var uidRace = -1
    var loc: GBLocation? = null

    // Type Factory (ships made without factory)
    fun makeStructure(_uidPlanet: Int, _uidRace: Int, _type: Int) {

        val planet = u.planet(_uidPlanet)
        val race = u.race(_uidRace)
        gbAssert { type == -1 }
        type = _type
        uidRace = race.uid
        // TODO Have caller give us a better location (or find one ourselves) for structure ?
        this.loc = GBLocation(planet, GBData.rand.nextInt(planet.width), GBData.rand.nextInt(planet.height))

    }

    fun findEmtpySector() : Int {

        //var sectors = planet.sectors.sortedBy { -it.population }

        return 0
    }

    // Ships that are made by a factory
    fun makeShip(uidFactory: Int, _type: Int) {

        val factory = u.ship(uidFactory)

        if (factory.health > 0) {
            gbAssert { type == -1 }
            type = _type
            uidShip = factory.uid
            uidRace = factory.uidRace
            if (GBData.shipsData[_type]!!.surface) {
                this.loc = GBLocation(
                    factory.loc.getPlanet()!!,
                    GBData.rand.nextInt(factory.loc.getPlanet()!!.width),
                    GBData.rand.nextInt(factory.loc.getPlanet()!!.height)
                )
            } else {
                this.loc = GBLocation(
                    factory.loc.getPlanet()!!,
                    GBData.PlanetOrbit,
                    GBData.rand.nextFloat() * 2 * PI.toFloat()
                )
            }
        } else {
            // Factory died since order was created
            // Leave type as is, at -1
        }
    }

    fun execute() {
        // Everything is a ship order at this time, no when() needed
        if (type != -1) {
            val ship = GBShip(u.getNextGlobalId(), type, uidRace, loc!!)
            ship.initializeShip()
            u.news.add("${ship.name} built on ${ship.loc.getPlanet()!!.name}.\n")
        } else {
            // Order bad, maybe because factory died since we ordered something.
        }
    }

}