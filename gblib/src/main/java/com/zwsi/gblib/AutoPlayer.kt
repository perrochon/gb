package com.zwsi.gblib

import com.zwsi.gblib.*
import com.zwsi.gblib.GBController.Companion.u

class AutoPlayer() {

    companion object {

        fun playBeetle() {

            GBLog.d("Programming Beetles in turn $u.turn")
            // TODO Quality there must be an easier way...
            val r = u.allRaces.values.find { it.idx == 2 }!!
            val now = u.turn + 1 // just in case we have a turn running....

            var code = {}

            code = {
                val p = r.getHome()
                GBController.u.makeFactory(p, r)
                GBLog.d("Ordered Factory")
            }
            GBScheduler.addInstructionAt(now, code)

            for (i in 1 until 10000) {
                code = {
                    val factory = r.getRaceShipsList().filter { it.idxtype == GBData.FACTORY }.firstOrNull()
                    if (factory != null) {
                        factory?.let { GBController.u.makePod(it) }
                        GBLog.d("Ordered Pod")
                    }
                }
                GBScheduler.addInstructionAt(now + 1 + i, code)

            }
            code = {
                for (pod in r.getRaceShipsList().filter { (it.idxtype == GBData.POD) && (it.dest == null) }) {
                    pod?.let {
                        GBController.u.flyShipLanded(
                            it,
                            u.allPlanets[GBData.rand.nextInt(u.allPlanets.size)]
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
            val r = u.allRaces.values.find { it.idx == 1 }!!
            val now = u.turn + 1 // just in case we have a turn running....

            var code = {}

            code = {
                val p = r.getHome()
                GBController.u.makeFactory(p, r)
                GBLog.d("Ordered Factory")
            }
            GBScheduler.addInstructionAt(now, code)

            for (i in 1..20) {
                code = {
                    val factory = r.getRaceShipsList().find { it.idxtype == GBData.FACTORY }
                    factory?.let { GBController.u.makeCruiser(it) }
                    GBLog.d("Ordered Cruiser")
                }
                GBScheduler.addInstructionAt(now + 1 + i * 10, code)
            }

            code = {
                // TODO Fix: Getting all ships, not just this race...
                val cruiser = r.getRaceShipsList().find {
                    ((it.idxtype == GBData.CRUISER) && (it.loc.level == GBLocation.LANDED))
                }
                val p = u.allPlanets[GBData.rand.nextInt(u.allPlanets.size)]
                val homeBeetle = u.allRaces.values.find { it.idx == 2 }!!.getHome()
                if (p != homeBeetle) {
                    cruiser?.let { GBController.u.flyShipOrbit(it, p) }
                } // else just try again next time this code runs...
                GBLog.d("Directed Cruiser")
            }
            GBScheduler.addInstructionAlways(code)

        }

        fun playTortoise() {

            GBLog.d("Programming Tortoise in  turn  $u.turn")
            val r = u.allRaces.values.find { it.idx == 3 }!!
            val now = u.turn + 1 // just in case we have a turn running....

            var code = {}

            code = {
                val p = r.getHome()
                GBController.u.makeFactory(p, r)
                GBLog.d("Ordered Factory")
            }
            GBScheduler.addInstructionAt(now, code)

            for (i in 1..20) {
                code = {
                    val factory = r.getRaceShipsList().find { it.idxtype == GBData.FACTORY }
                    factory?.let { GBController.u.makeCruiser(it) }
                    GBLog.d("Ordered Cruiser")
                }
                GBScheduler.addInstructionAt(now + 1 + i * 10, code)
            }

            code = {
                // TODO Fix: Getting all ships, not just this race...
                val cruiser = r.getRaceShipsList().find {
                    ((it.idxtype == GBData.CRUISER) && (it.loc.level == GBLocation.LANDED))
                }
                val p = u.allPlanets[GBData.rand.nextInt(u.allPlanets.size)]
                val homeBeetle = u.allRaces.values.find { it.idx == 2 }!!.getHome()
                if (p != homeBeetle) {
                    cruiser?.let { GBController.u.flyShipOrbit(it, p) }
                } // else just try again next time this code runs...

                GBLog.d("Directed Cruiser")
            }
            GBScheduler.addInstructionAlways(code)
        }

    }
}