// Copyright 2018-2019 Louis Perrochon. All rights reserved

// Race AIs are here

package com.zwsi.gblib

import com.zwsi.gblib.GBController.Companion.u
import kotlin.math.PI

class GBAutoPlayer() {

    companion object {

        fun playXenos() {
            val r = u.race(0)
            GBLog.d("Playing Xenos in turn $u.turn")

        }

        fun playImpi() {

            val r = u.race(1)
            GBLog.d("Playing Impi in turn $u.turn")

            // Launch landed cruisers with no destination to orbit. They do little on planet...
            for (cruiser in r.raceShips.filter {
                (it.idxtype == GBData.CRUISER) && (it.loc.level == GBLocation.LANDED) && it.dest == null
            }) {
                val loc = GBLocation(
                    cruiser.loc.getPlanet()!!,
                    GBData.PlanetOrbit,
                    GBData.rand.nextFloat() * 2 * PI.toFloat()
                )
                GBLog.d("Setting Destination of " + cruiser.name + " to " + cruiser.loc.getPlanet()!!.name)
                cruiser.dest = loc

            }
            GBLog.d("Directed Cruisers")
        }

        fun playBeetle() {

            GBLog.d("Playing Beetles in turn $u.turn")
            val r = u.race(2) // TODO Look this up

            // Find factory and order a pod. If we don't have a factory order one at home.
            val factory = r.raceShips.filter { it.idxtype == GBData.FACTORY }.firstOrNull()
            if (factory == null) {
                val order = GBOrder()
                order.makeFactory(r.getHome().uid, r.uid)
                u.orders.add(order)
                GBLog.d("Ordered Factory")
            } else {
                val order = GBOrder()
                order.makePod(factory.uid)
                u.orders.add(order)
                GBLog.d("Ordered Pod")
            }

            // Send any pod that doesn't have a destination to some random planet
            for (pod in r.raceShips.filter { (it.idxtype == GBData.POD) && (it.dest == null) }) {

                // TODO Fun: Other than random. E.g. order all planets by direction to create a nice spiral :-)
                val planet = u.planets[GBData.rand.nextInt(u.planets.size)]!!

                val loc = GBLocation(planet, 0, 0) // TODO Have caller give us a better location?
                GBLog.d("Setting Destination of " + pod.name + " to " + planet.name)
                pod.dest = loc


            }
            GBLog.d("Directed Pods")
        }


        fun playTortoise() {

            // Find factory and order a cruiser, up to a certain number of ships. If we don't have a factory order one
            GBLog.d("Playing Tortoise in  turn  $u.turn")
            val r = u.race(3)

            val factory = r.raceShips.filter { it.idxtype == GBData.FACTORY }.firstOrNull()
            if (factory == null) {
                val order = GBOrder()
                order.makeFactory(r.getHome().uid, r.uid)
                u.orders.add(order)
                GBLog.d("Ordered Factory")
            } else {
                if (r.raceShips.size < 31) {
                    val order = GBOrder()
                    order.makeCruiser(factory.uid)
                    u.orders.add(order)
                    GBLog.d("Ordered Cruiser")
                }
            }

            // Send any cruiser that's landed (likely freshly made) to some random planet, but not the beetle home
            val homeBeetle = u.race(2).getHome()
            for (cruiser in r.raceShips.filter {
                (it.idxtype == GBData.CRUISER) && (it.loc.level == GBLocation.ORBIT) && it.loc.uidRef == r.uidHomePlanet
            }.drop(5)) {
                val planet = u.planets[GBData.rand.nextInt(u.planets.size)]!!
                if (planet != homeBeetle) {
                    val loc = GBLocation(planet, GBData.PlanetOrbit, GBData.rand.nextFloat() * 2 * PI.toFloat())
                    GBLog.d("Setting Destination of " + cruiser.name + " to " + planet.name)
                    cruiser.dest = loc

                } // else just try again next time this code runs...
            }
            GBLog.d("Directed Cruisers")

        }
    }
}