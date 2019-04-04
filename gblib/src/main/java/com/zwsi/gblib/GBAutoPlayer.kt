// Copyright 2018-2019 Louis Perrochon. All rights reserved

// Race AIs are here

package com.zwsi.gblib

import com.zwsi.gblib.GBController.Companion.u
import com.zwsi.gblib.GBData.Companion.BATTLESTAR
import com.zwsi.gblib.GBData.Companion.CRUISER
import com.zwsi.gblib.GBData.Companion.FACTORY
import com.zwsi.gblib.GBData.Companion.POD
import com.zwsi.gblib.GBData.Companion.SHUTTLE
import com.zwsi.gblib.GBData.Companion.STATION
import com.zwsi.gblib.GBLocation.Companion.ORBIT
import kotlin.math.PI

const val DEPLOYMENT_TOPLANET = 0
const val DEPLOYMENT_RANDOM = 1
const val DEPLOYMENT_ATTACK = 2
const val DEPLOYMENT_GAS = 3


class GBAutoPlayer() {

    var autoPlayers = arrayOf(true, true, true, true, true, true)

    private fun findOrOrderFactory(r: GBRace): GBShip? {
        val factory = r.raceShips.filter { it.idxtype == FACTORY }.sortedBy { -it.uid }.firstOrNull()
        if (factory == null) {
            val order = GBOrder()
            order.makeStructure(r.getHome().uid, r.uid, FACTORY)
            u.orders.add(order)
        }
        return factory
    }

    private fun orderFactories(r: GBRace) {
        for (planet in u.planets.values) {
            if (r.uid in planet.planetUidRaces) {
                if (planet.landedShips.filter { it.uidRace == r.uid && it.idxtype == FACTORY }.isEmpty()) {
                    val order = GBOrder()
                    order.makeStructure(planet.uid, r.uid, FACTORY)
                    u.orders.add(order)
                }
            }
        }
    }

    private fun orderShips(
        r: GBRace,
        factory: GBShip,
        pod: Int,
        cruiser: Int,
        station: Int,
        shuttle: Int,
        battlestar: Int,
        other: Int
    ) {
        when {
//                r.raceShips.filter { it.idxtype == POD }.size < 0 -> { // FIXME Pods creation fails anyway, so let's not attempt to do them here
//                    val order = GBOrder()
//                    order.makeShip(factory.uid, POD)
//                    u.orders.add(order)
//                }
            r.raceShips.filter { it.idxtype == CRUISER }.size < cruiser -> {
                val order = GBOrder()
                order.makeShip(factory.uid, CRUISER)
                u.orders.add(order)
            }
            r.raceShips.filter { it.idxtype == STATION }.size < station -> {
                val order = GBOrder()
                order.makeShip(factory.uid, STATION)
                u.orders.add(order)
            }
            r.raceShips.filter { it.idxtype == SHUTTLE }.size < shuttle -> {
                val order = GBOrder()
                order.makeShip(factory.uid, SHUTTLE)
                u.orders.add(order)
            }
            r.raceShips.filter { it.idxtype == BATTLESTAR }.size < battlestar -> {
                val order = GBOrder()
                order.makeShip(factory.uid, BATTLESTAR)
                u.orders.add(order)
            }
            else -> {
                val order = GBOrder()
                order.makeShip(factory.uid, other)
                u.orders.add(order)
            }
        }

    }

    private fun deployShips(r: GBRace, type: Int, targetPlanet: GBPlanet? = null) {

        for (ship in r.raceShips.filter {
            (it.idxtype == CRUISER || it.idxtype == BATTLESTAR || it.idxtype == SHUTTLE)
                    && it.loc.level == ORBIT && it.loc.uidRef == r.uidHomePlanet
        }) {
            val planet = when (type) {
                DEPLOYMENT_TOPLANET -> targetPlanet

                DEPLOYMENT_ATTACK ->
                    if (u.turn % 5 == 0) {
                        u.race(0).getHome()
                    } else {
                        u.planets.values.shuffled().first()
                    }

                DEPLOYMENT_GAS -> u.planets.values.filter { it.idxtype == 1 }.shuffled().firstOrNull()

                else -> u.planets.values.shuffled().first()
            }
            if (planet != null) {
                val loc = GBLocation(planet, GBData.PlanetOrbit, GBData.rand.nextFloat() * 2 * PI.toFloat())
                GBLog.d("Setting Destination of " + ship.name + " to " + planet.name)
                ship.dest = loc

            }
        } // else just try again next time this code runs...
    }

    fun playXenos(r: GBRace) {

        GBLog.d("Playing ${r.name} in turn ${u.turn}")

        // Find factory and order a cruiser, up to a certain number of shipsData. If we don't have a factory order one
        val factory = findOrOrderFactory(r) ?: return

        orderShips(r, factory, 20, 5, 10, 0, 0, CRUISER)

        deployShips(r, DEPLOYMENT_RANDOM)

    }

    fun playImpi(r: GBRace) {

        GBLog.d("Playing ${r.name} in turn ${u.turn}")

        // Find factory and order a cruiser, up to a certain number of shipsData. If we don't have a factory order one
        val factory = findOrOrderFactory(r) ?: return

        orderShips(r, factory, 20, 5, 5, 0, 0, BATTLESTAR)

        deployShips(r, DEPLOYMENT_RANDOM)

    }

    fun playBeetle(r: GBRace) {

        // TODO: Beetles just make a new queen/headquarter if they lose it...
        GBLog.d("Playing Beetles in turn ${u.turn} (${u.ship(r.uidHeadquarters).health})")

        // Find factory and order a pod. If we don't have a factory order one at home.
        val factory = findOrOrderFactory(r) ?: return

        orderFactories(r)

        val order = GBOrder()
        order.makeShip(factory.uid, POD)
        u.orders.add(order)
        GBLog.d("Ordered Pod")

        // Send any pod that doesn't have a destination to some random planet
        for (pod in r.raceShips.filter { (it.idxtype == GBData.POD) && (it.dest == null) }) {

            val planet = u.planets.values.filter { it.orbitShips.size == 0 }.shuffled().firstOrNull()
                ?: u.planets.values.sortedBy { it.orbitShips.size }.first()

            pod.dest = GBLocation(planet, 0, 0)
            GBLog.d("Setting Destination of " + pod.name + " to " + planet.name)
        }
        GBLog.d("Directed Pods")
    }

    fun playTortoise(r: GBRace) {

        GBLog.d("Playing ${r.name} in turn ${u.turn}")

        // Find factory and order a cruiser, up to a certain number of shipsData. If we don't have a factory order one
        val factory = findOrOrderFactory(r) ?: return

        orderShips(r, factory, 0, 31, 5, 5, 5, CRUISER)

        deployShips(r, DEPLOYMENT_ATTACK)

    }

    private val toolsTargets = mutableListOf<GBPlanet>()

    fun playTools(r: GBRace) {

        GBLog.d("Playing ${r.name} in turn ${u.turn}")

        if (toolsTargets.isEmpty()) {
            toolsTargets.addAll(u.planets.values.sortedBy {
                it.loc.getLoc().distance(r.getHome().loc.getLoc())
            })
        }

        val factory = findOrOrderFactory(r) ?: return

        orderShips(r, factory, 20, 10, 5, 5, 5, BATTLESTAR)

        if (u.turn % 2 == 0) {
            deployShips(r, DEPLOYMENT_TOPLANET, toolsTargets.first())
            toolsTargets.removeAt(0)
        }

    }

    fun playGhosts(r: GBRace) {

        GBLog.d("Playing ${r.name} in turn ${u.turn}")

        val factory = findOrOrderFactory(r) ?: return

        orderShips(r, factory, 50, 5, 5, 10, 5, SHUTTLE)

        deployShips(r, DEPLOYMENT_GAS)

    }

}