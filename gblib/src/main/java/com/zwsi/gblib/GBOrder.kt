// Copyright 2018-2019 Louis Perrochon. All rights reserved
//
// // Location. Can be planet surface, planet orbit, system space, or deep space. All have different coordinates, etc.

package com.zwsi.gblib

import com.zwsi.gblib.GBController.Companion.u
import com.zwsi.gblib.GBData.Companion.FACTORY
import com.zwsi.gblib.GBLocation.Companion.LANDED
import com.zwsi.gblib.GBLog.gbAssert
import kotlin.math.PI


class GBOrder {

    var type = -1
    var uidShip = -1
    var uidRace = -1
    lateinit var loc: GBLocation

    // Type Factory (ships made without factory)
    fun makeFactory(_uidPlanet: Int, _uidRace: Int) {

        val planet = u.planet(_uidPlanet)
        val race = u.race(_uidRace)
        // TODO Have caller give us a better location (or find one ourselves) for factory ?
        val loc =
            GBLocation(planet, GBData.rand.nextInt(planet.width), GBData.rand.nextInt(planet.height))

        gbAssert { type == -1 }
        type = FACTORY
        uidRace = race.uid
        gbAssert { loc.level == LANDED }
        this.loc = loc
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
        }
    }

    fun execute() {
        // Everything is a ship order at this time, no when() needed
        val ship = GBShip(u.getNextGlobalId(), type, uidRace, loc)
        ship.initializeShip()
        u.news.add("${ship.name} built on ${ship.loc.getPlanet()!!.name}.\n")
    }

}