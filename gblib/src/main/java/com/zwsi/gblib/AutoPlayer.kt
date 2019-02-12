// Copyright 2018-2019 Louis Perrochon. All rights reserved

// Race AIs are here

package com.zwsi.gblib

import com.zwsi.gblib.GBController.Companion.u

class AutoPlayer() {

    companion object {

        fun playBeetle() {

            GBLog.d("Playing Beetles in turn $u.turn")
            val r = u.race(2)

            // Find factory and order a pod. If we don't have a factory order one at home.
            val factory = r.raceShipsList.filter { it.idxtype == GBData.FACTORY }.firstOrNull()
            if (factory == null) {
                val p = r.getHome()
                GBController.makeFactory(p, r)
                GBLog.d("Ordered Factory")
            } else {
                factory.let { GBController.makePod(it) }
                GBLog.d("Ordered Pod")
            }

            // Send any pod that doesn't have a destination to some random planet
            for (pod in r.raceShipsList.filter { (it.idxtype == GBData.POD) && (it.dest == null) }) {
                pod.let {
                    GBController.flyShipLanded(
                        it,
                        u.allPlanets[GBData.rand.nextInt(u.allPlanets.size)]!!
                    )
                }
            }
            GBLog.d("Directed Pods")
        }

        fun playImpi() {

            GBLog.d("Playing Impi in turn $u.turn")
            val r = u.race(1)

            // Find factory and order a cruiser, up to a certain number of ships. If we don't have a factory order one
            val factory = r.raceShipsList.filter { it.idxtype == GBData.FACTORY }.firstOrNull()
            if (factory == null) {
                val p = r.getHome()
                GBController.makeFactory(p, r)
                GBLog.d("Ordered Factory")
            } else {
                if (r.raceShipsList.size < 31) {
                    factory.let { GBController.makeCruiser(it) }
                    GBLog.d("Ordered Cruiser")
                }
            }

            // Send any cruiser that's landed (likely freshly made) to some random planet, but not the beetle home
            val homeBeetle = u.race(2).getHome()
            for (cruiser in r.raceShipsList.filter { (it.idxtype == GBData.CRUISER) && (it.loc.level == GBLocation.LANDED) }) {
                val p = u.allPlanets[GBData.rand.nextInt(u.allPlanets.size)]!!
                if (p != homeBeetle) {
                    cruiser.let { GBController.flyShipOrbit(it, p) }
                } // else just try again next time this code runs...
            }
            GBLog.d("Directed Cruisers")
        }

        fun playTortoise() {

            // Find factory and order a cruiser, up to a certain number of ships. If we don't have a factory order one
            GBLog.d("Playing Tortoise in  turn  $u.turn")
            val r = u.race(3)

            val factory = r.raceShipsList.filter { it.idxtype == GBData.FACTORY }.firstOrNull()
            if (factory == null) {
                val p = r.getHome()
                GBController.makeFactory(p, r)
                GBLog.d("Ordered Factory")
            } else {
                if (r.raceShipsList.size < 31) {
                    factory.let { GBController.makeCruiser(it) }
                    GBLog.d("Ordered Cruiser")
                }
            }

            // Send any cruiser that's landed (likely freshly made) to some random planet, but not the beetle home
            val homeBeetle = u.race(2).getHome()
            for (cruiser in r.raceShipsList.filter { (it.idxtype == GBData.CRUISER) && (it.loc.level == GBLocation.LANDED) }) {
                val p = u.allPlanets[GBData.rand.nextInt(u.allPlanets.size)]!!
                if (p != homeBeetle) {
                    cruiser.let { GBController.flyShipOrbit(it, p) }
                } // else just try again next time this code runs...
            }
            GBLog.d("Directed Cruisers")

        }
    }
}