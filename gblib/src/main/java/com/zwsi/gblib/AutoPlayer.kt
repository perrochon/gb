// Copyright 2018-2019 Louis Perrochon. All rights reserved

// Race AIs are here

 package com.zwsi.gblib

import com.zwsi.gblib.*
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

            GBLog.d("Programming Beetles in turn $u.turn")
            val r = u.race(2)
            val now = u.turn // just in case we have a turn running.... // FIXME removed +1 until more robust...

            var code = {
                val p = r.getHome()
                GBController.u.makeFactory(p, r)
                GBLog.d("Ordered Factory")
            }
            GBScheduler.addInstructionAt(now, code)

            for (i in 1 until 10000) { // FIXME scheduling 10,000 builds? Better to add one repeating...
                code = {
                    val factory = r.raceShipsList.filter { it.idxtype == GBData.FACTORY }.firstOrNull()
                    if (factory != null) {
                        factory.let { GBController.u.makePod(it) }
                        GBLog.d("Ordered Pod")
                    }
                }
                GBScheduler.addInstructionAt(now + 1 + i, code)

            }
            code = {
                for (pod in r.raceShipsList.filter { (it.idxtype == GBData.POD) && (it.dest == null) }) {
                    pod.let {
                        GBController.u.flyShipLanded(
                            it,
                            u.allPlanets[GBData.rand.nextInt(u.allPlanets.size)]!!
                        )
                    }
                }
                GBLog.d("Directed Pod")
            }
            //GBScheduler.addInstructionAlways(code)
            GBScheduler.addInstructionEvery(25, code)


        }

        fun playImpi() {

            GBLog.d("Programming Impi in turn $u.turn")
            val r = u.race(1)
            val now = u.turn + 1 // just in case we have a turn running....

            var code = {
                val p = r.getHome()
                GBController.u.makeFactory(p, r)
                GBLog.d("Ordered Factory")
            }
            GBScheduler.addInstructionAt(now, code)

            for (i in 1..20) {
                code = {
                    val factory = r.raceShipsList.find { it.idxtype == GBData.FACTORY }
                    factory?.let { GBController.u.makeCruiser(it) }
                    GBLog.d("Ordered Cruiser")
                }
                GBScheduler.addInstructionAt(now + 1 + i * 10, code)
            }

            code = {
                val cruiser = r.raceShipsList.find {
                    ((it.idxtype == GBData.CRUISER) && (it.loc.level == GBLocation.LANDED))
                }
                if (cruiser != null) {
                    val p = u.allPlanets[GBData.rand.nextInt(u.allPlanets.size)]!!
                    val homeBeetle = u.race(2).getHome()
                    if (p != homeBeetle) {
                        cruiser.let { GBController.u.flyShipOrbit(it, p) }
                    } // else just try again next time this code runs...
                    GBLog.d("Directed Cruiser")
                }
            }
            GBScheduler.addInstructionAlways(code)

        }

        fun playTortoise() {

            GBLog.d("Programming Tortoise in  turn  $u.turn")
            val r = u.race(3)
            val now = u.turn + 1 // just in case we have a turn running....

            var code = {
                val p = r.getHome()
                GBController.u.makeFactory(p, r)
                GBLog.d("Ordered Factory")
            }
            GBScheduler.addInstructionAt(now, code)

            for (i in 1..20) {
                code = {
                    val factory = r.raceShipsList.find { it.idxtype == GBData.FACTORY }
                    factory?.let { GBController.u.makeCruiser(it) }
                    GBLog.d("Ordered Cruiser")
                }
                GBScheduler.addInstructionAt(now + 1 + i * 10, code)
            }

            code = {
                val cruiser = r.raceShipsList.find {
                    ((it.idxtype == GBData.CRUISER) && (it.loc.level == GBLocation.LANDED))
                }
                if (cruiser != null) {
                    val p = u.allPlanets[GBData.rand.nextInt(u.allPlanets.size)]!!
                    val homeBeetle = u.race(2).getHome()
                    if (p != homeBeetle) {
                        cruiser.let { GBController.u.flyShipOrbit(it, p) }
                    } // else just try again next time this code runs...
                    GBLog.d("Directed Cruiser")
                }
            }
            GBScheduler.addInstructionAlways(code)
        }

    }
}