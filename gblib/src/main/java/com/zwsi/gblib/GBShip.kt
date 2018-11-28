// Copyright 2018 Louis Perrochon. All rights reserved
//
// // Inspiration
// https://github.com/kaladron/galactic-bloodshed/blob/master/data/exam.dat
// https://github.com/kaladron/galactic-bloodshed/blob/master/data/ship.dat

package com.zwsi.gblib

import com.zwsi.gblib.GBController.Companion.universe
import com.zwsi.gblib.GBDebug.gbAssert
import com.zwsi.gblib.GBLocation.Companion.DEEPSPACE
import com.zwsi.gblib.GBLocation.Companion.LANDED
import com.zwsi.gblib.GBLocation.Companion.ORBIT
import com.zwsi.gblib.GBLocation.Companion.SYSTEM
import org.omg.CORBA.ORB

class GBShip(val idxtype: Int, val race: GBRace, var loc: GBLocation) {

    val id: Int
    val uid: Int
    val rid: Int
    val name: String
    val type: String
    val speed: Int

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
            DEEPSPACE-> {
                universe.universeShips.add(this)
            }
            else -> {
                gbAssert("Bad Parameters for ship placement $loc", { false })
            }

        }

        type = GBData.getShipType(idxtype)
        name = type + " " + race.raceShips.indexOf(this)
        speed = GBData.getShipSpeed(idxtype)
    }

    fun moveShip(loc: GBLocation) {

        GBDebug.l3("Moving Ship from" + this.loc.getLocDesc() + "to" + loc.getLocDesc())


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
        this.loc = loc

    }


    fun getStar(): GBStar? {
        return loc.getStar()
    }

}

