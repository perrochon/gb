// Copyright 2018 Louis Perrochon. All rights reserved
//
// // Inspiration
// https://github.com/kaladron/galactic-bloodshed/blob/master/data/exam.dat
// https://github.com/kaladron/galactic-bloodshed/blob/master/data/ship.dat

package com.zwsi.gblib

import com.zwsi.gblib.GBController.Companion.universe
import com.zwsi.gblib.GBLocation.Companion.DEEPSPACE
import com.zwsi.gblib.GBLocation.Companion.LANDED
import com.zwsi.gblib.GBLocation.Companion.ORBIT
import com.zwsi.gblib.GBLocation.Companion.SYSTEM
import com.zwsi.gblib.GBLog.gbAssert
import java.util.*

class GBShip(val idxtype: Int, val race: GBRace, var loc: GBLocation) {

    val id: Int
    val uid: Int
    val rid: Int
    val name: String
    val type: String
    val speed: Int

    var dest: GBLocation? = null
    val trail = LinkedList<GBxy>()

    init {
        id = GBData.getNextGlobalId()
        universe.allShips.add(this)
        uid = universe.allShips.indexOf(this)

        race.raceShips.add(this)
        rid = race.raceShips.indexOf(this)

        when (loc.level) {
            LANDED -> {
                loc.getPlanet()!!.landedShips.add(this)
            }
            ORBIT -> {
                loc.getPlanet()!!.orbitShips.add(this)
            }
            SYSTEM -> {
                loc.getStar()!!.starShips.add(this)
            }
            DEEPSPACE -> {
                universe.universeShips.add(this)
            }
            else -> {
                gbAssert("Bad Parameters for ship placement $loc", { false })
            }

        }

        type = GBData.getShipType(idxtype)
        name = "S" + race.raceShips.indexOf(this)
        speed = GBData.getShipSpeed(idxtype)
    }

    fun changeShipLocation(loc: GBLocation) {

        GBLog.d("Moving " + name + " from " + this.loc.getLocDesc() + " to " + loc.getLocDesc())

        // TODO no need to always do these whens, e.g. if things are the same, no need to remove and add
        when (this.loc.level) {
            LANDED -> {
                this.loc.getPlanet()!!.landedShips.remove(this)
            }
            ORBIT -> {
                this.loc.getPlanet()!!.orbitShips.remove(this)
            }
            SYSTEM -> {
                this.loc.getStar()!!.starShips.remove(this)
            }
            DEEPSPACE -> {
                universe.universeShips.remove(this)
            }
            else -> {
                gbAssert("Bad Parameters for ship removement $loc", { false })
            }
        }
        when (loc.level) {
            LANDED -> {
                if (idxtype == 1) {
                    // This is a pod, they populate, then destroy
                    // TODO How to properly destro ships?
                    // We could just remove it from all lists. But there are fragments etc. that keep state
                    // With persistence, we may need more work
                    // universe.allShips.remove(this)
                    // race.raceShips.remove(this)
                    universe.landPopulation(this.loc.getPlanet()!!, race.uid, 1)
                } else {
                    loc.getPlanet()!!.landedShips.add(this)
                }
            }
            ORBIT -> {
                loc.getPlanet()!!.orbitShips.add(this)
            }
            SYSTEM -> {
                loc.getStar()!!.starShips.add(this)
            }
            DEEPSPACE -> {
                universe.universeShips.add(this)
            }
            else -> {
                gbAssert("Bad Parameters for ship placement $loc", { false })
            }
        }
        this.loc = loc

    }

    fun doShip() {
        moveShip()
    }

    fun moveShip() {

        if (trail.size > 10) {
            trail.removeFirst()
        }

        if (dest == null) {
            return
        }

        trail.addLast(loc.getLoc())

        val dest = this.dest!!
        val dxy = dest.getLoc()       // use getLoc to get universal (x,y)
        val sxy = loc.getLoc()        // center of planet for landed and orbit

        GBLog.d("Attempting to fly" + name + " from " + this.loc.getLocDesc() + " to " + dest.getLocDesc())

        if (loc.level == LANDED) { // We are landed

            GBLog.d(name + " is landed")

            if ((dest.level != loc.level) || (dest.refUID != loc.refUID)) { // landed and we need to get to orbit

                GBLog.d("launching " + name)

                var next = GBLocation(loc.getPlanet()!!, 1f, 1f)
                changeShipLocation(next)
                GBLog.d("launching " + name)
                universe.news.add("Launched $name to ${loc.getLocDesc()}.\n")

                return
            }

            // here we will deal with surface to surface moves on the same planet

            return

        } else if ((loc.level == ORBIT) && (loc.refUID == dest.refUID)) {

            GBLog.d(name + " is in orbit at destination. Landing.")

            // in orbit at destination so we need to land
            changeShipLocation(dest)
            universe.news.add("$name ${loc.getLocDesc()}.\n")
            this.dest = null

            return

        } else {
            // we are not LANDED, so either in ORBIT, in SYTEM, or in DEEPSPACE

            var distance = sxy.distance(dxy)

            if (distance < speed) { // we will arrive at a planet (i.e. in Orbit) this turn. Can only fly to planets (right now)

                var next = GBLocation(dest.getPlanet()!!, 1f, 1f)
                changeShipLocation(next)
                universe.news.add("$name arrived in ${loc.getLocDesc()}.\n")

                if (dest.level == ORBIT) {
                    this.dest = null

                }
                return
            }

            var nxy = sxy.towards(dxy, speed.toFloat())


            GBLog.d("Flying from (${sxy.x}, ${sxy.y}) direction (${dxy.x}, ${dxy.y}) until (${nxy.x}, ${nxy.y}) at speed $speed\n")

            if (loc.level == DEEPSPACE) {


                if (distance < 15f) { // we arrived at destination

                    // TODO check if destination is the system, in which case we would stop

                    var next = GBLocation(dest.getStar()!!, nxy.x, nxy.y, true)

                    changeShipLocation(next)

                    GBLog.d(" Arrived in System")

                    universe.news.add("$name arrived in ${loc.getLocDesc()}. ( ${loc.x.toInt()} , ${loc.y.toInt()} )\n")
                    return

                } else {
                    var next = GBLocation(nxy.x, nxy.y)
                    changeShipLocation(next)

                    GBLog.d(" Flying Deep Space")


                    //universe.news.add("$name moved in ${loc.getLocDesc()}. ( ${loc.x} , ${loc.y} )\n")
                    return

                }


            } else {

                var distanceToStar = sxy.distance(loc.getStar()!!.loc.getLoc())

                if (distanceToStar > 15f) {  // we left the system

                    var next = GBLocation(nxy.x, nxy.y)
                    changeShipLocation(next)

                    GBLog.d("Left System")

                    universe.news.add("$name entered ${loc.getLocDesc()}. ( ${loc.x.toInt()} , ${loc.y.toInt()} )\n")
                    return
                } else {

                    var next = GBLocation(loc.getStar()!!, nxy.x, nxy.y, true)
                    changeShipLocation(next)

                    GBLog.d("Flying insystem ")

                    //universe.news.add("$name moved in ${loc.getLocDesc()}. ( ${loc.x.toInt()} , ${loc.y.toInt()} )\n")
                    return

                }
            }
        }
    }


    fun getStar(): GBStar? {
        return loc.getStar()
    }

}

