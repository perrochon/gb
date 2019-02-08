// Copyright 2018-2019 Louis Perrochon. All rights reserved

// Race AIs are here

package com.zwsi.gblib

import com.zwsi.gblib.GBController.Companion.u

class AutoPlayer() {

    // FIXME Persistence. All Instructions will be wiped on restore from JSON.
    // So need to keep some state in a different, serializable class. E.g. how many ships produced.
    // Implementing per race ship counters for naming would help with all code we have so far.
    // Basically, all the code below has to be idempotent.
    // Then we have a flag that states which races are playing. Upon restore we call playXXX() again
    // and they are back in the game.

    companion object {

        fun playBeetle() {

            GBLog.d("Playing Beetles in turn $u.turn")
            val r = u.race(2)

            val factory = r.raceShipsList.filter { it.idxtype == GBData.FACTORY }.firstOrNull()
            if (factory == null) {
                val p = r.getHome()
                GBController.u.makeFactory(p, r)
                GBLog.d("Ordered Factory")
            } else {
                factory.let { GBController.u.makePod(it) }
                GBLog.d("Ordered Pod")
            }
            for (pod in r.raceShipsList.filter { (it.idxtype == GBData.POD) && (it.dest == null) }) {
                pod.let {
                    GBController.u.flyShipLanded(
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

            val factory = r.raceShipsList.filter { it.idxtype == GBData.FACTORY }.firstOrNull()
            if (factory == null) {
                val p = r.getHome()
                GBController.u.makeFactory(p, r)
                GBLog.d("Ordered Factory")
            } else {
                if (r.raceShipsList.size < 31) {
                    factory.let { GBController.u.makeCruiser(it) }
                    GBLog.d("Ordered Cruiser")
                }
            }

            val homeBeetle = u.race(2).getHome()
            for (cruiser in r.raceShipsList.filter { (it.idxtype == GBData.CRUISER) && (it.loc.level == GBLocation.LANDED) }) {
                val p = u.allPlanets[GBData.rand.nextInt(u.allPlanets.size)]!!
                if (p != homeBeetle) {
                    cruiser.let { GBController.u.flyShipOrbit(it, p) }
                } // else just try again next time this code runs...
            }
            GBLog.d("Directed Cruisers")
        }

        fun playTortoise() {

            GBLog.d("Playing Tortoise in  turn  $u.turn")
            val r = u.race(1)

            val factory = r.raceShipsList.filter { it.idxtype == GBData.FACTORY }.firstOrNull()
            if (factory == null) {
                val p = r.getHome()
                GBController.u.makeFactory(p, r)
                GBLog.d("Ordered Factory")
            } else {
                if (r.raceShipsList.size < 31) {
                    factory.let { GBController.u.makeCruiser(it) }
                    GBLog.d("Ordered Cruiser")
                }
            }

            val homeBeetle = u.race(2).getHome()
            for (cruiser in r.raceShipsList.filter { (it.idxtype == GBData.CRUISER) && (it.loc.level == GBLocation.LANDED) }) {
                val p = u.allPlanets[GBData.rand.nextInt(u.allPlanets.size)]!!
                if (p != homeBeetle) {
                    cruiser.let { GBController.u.flyShipOrbit(it, p) }
                } // else just try again next time this code runs...
            }
            GBLog.d("Directed Cruisers")

        }
    }
}