// Copyright 2018-2019 Louis Perrochon. All rights reserved

// Race AIs are here

package com.zwsi.gblib

import com.zwsi.gblib.GBController.Companion.u

class AutoPlayer() {

    companion object {

        fun playBeetle() {

            GBLog.d("Playing Beetles in turn $u.turn")
            val r = u.race(2) // TODO Look this up

            // Find factory and order a pod. If we don't have a factory order one at home.
            val factory = r.raceShips.filter { it.idxtype == GBData.FACTORY }.firstOrNull()
            if (factory == null) {
                val p = r.getHome()
                GBController.makeFactory(p.uid, r.uid)
                GBLog.d("Ordered Factory")
            } else {
                GBController.makePod(factory.uid)
                GBLog.d("Ordered Pod")
            }

            // Send any pod that doesn't have a destination to some random planet
            for (pod in r.raceShips.filter { (it.idxtype == GBData.POD) && (it.dest == null) }) {
                // TODO Fun: Other than random. E.g. order all planets by direction to create a nice spiral :-)
                GBController.flyShipLanded(pod.uid, u.planets[GBData.rand.nextInt(u.planets.size)]!!.uid)
            }
            GBLog.d("Directed Pods")
        }

        fun playImpi() {

            GBLog.d("Playing Impi in turn $u.turn")
            val r = u.race(1)

            // Find factory and order a cruiser, up to a certain number of ships. If we don't have a factory order one
            val factory = r.raceShips.filter { it.idxtype == GBData.FACTORY }.firstOrNull()
            if (factory == null) {
                val p = r.getHome()
                GBController.makeFactory(p.uid, r.uid)
                GBLog.d("Ordered Factory")
            } else {
                if (r.raceShips.size < 31) {
                    GBController.makeCruiser(factory.uid)
                    GBLog.d("Ordered Cruiser")
                }
            }

            // Send any cruiser that's landed (likely freshly made) to some random planet, but not the beetle home
            val homeBeetle = u.race(2).getHome()
            for (cruiser in r.raceShips.filter { (it.idxtype == GBData.CRUISER) && (it.loc.level == GBLocation.LANDED) }) {
                val p = u.planets[GBData.rand.nextInt(u.planets.size)]!!
                if (p != homeBeetle) {
                    GBController.flyShipOrbit(cruiser.uid, p.uid)
                } // else just try again next time this code runs...
            }
            GBLog.d("Directed Cruisers")
        }

        fun playTortoise() {

            // Find factory and order a cruiser, up to a certain number of ships. If we don't have a factory order one
            GBLog.d("Playing Tortoise in  turn  $u.turn")
            val r = u.race(3)

            val factory = r.raceShips.filter { it.idxtype == GBData.FACTORY }.firstOrNull()
            if (factory == null) {
                val p = r.getHome()
                GBController.makeFactory(p.uid, r.uid)
                GBLog.d("Ordered Factory")
            } else {
                if (r.raceShips.size < 31) {
                    GBController.makeCruiser(factory.uid)
                    GBLog.d("Ordered Cruiser")
                }
            }

            // Send any cruiser that's landed (likely freshly made) to some random planet, but not the beetle home
            val homeBeetle = u.race(2).getHome()
            for (cruiser in r.raceShips.filter { (it.idxtype == GBData.CRUISER) && (it.loc.level == GBLocation.LANDED) }) {
                val p = u.planets[GBData.rand.nextInt(u.planets.size)]!!
                if (p != homeBeetle) {
                    GBController.flyShipOrbit(cruiser.uid, p.uid)
                } // else just try again next time this code runs...
            }
            GBLog.d("Directed Cruisers")

        }
    }
}