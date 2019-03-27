// Copyright 2018-2019 Louis Perrochon. All rights reserved
//
// // Location. Can be planet surface, planet orbit, system space, or deep space. All have different coordinates, etc.

package com.zwsi.gblib

import com.zwsi.gblib.GBController.Companion.u
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

        val sector = planet.emptySector()
        if (sector != null) {
            this.loc = GBLocation(planet, sector.x, sector.y)
        } else {
            this.loc = GBLocation(planet, 1, 1)
        }
    }

    fun findEmtpySector(): Int {

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
                val sector = factory.loc.getPlanet()!!.emptySector()
                if (sector != null) {
                    this.loc = GBLocation(factory.loc.getPlanet()!!, sector.x, sector.y)
                } else {
                    // TODO Do not silently fail to build anything
                    // this.loc = GBLocation(factory.loc.getPlanet()!!, 0, 0)
                }
            } else {
                this.loc = GBLocation(
                    factory.loc.getPlanet()!!,
                    GBData.PlanetOrbit,
                    GBData.rand.nextFloat() * 2 * PI.toFloat()
                )
            }
        } else {
            // Factory died since order was created
            // TODO Do not silently fail to build anything
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