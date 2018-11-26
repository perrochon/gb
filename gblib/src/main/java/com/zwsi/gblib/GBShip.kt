// Copyright 2018 Louis Perrochon. All rights reserved
//
// // Inspiration
// https://github.com/kaladron/galactic-bloodshed/blob/master/data/exam.dat
// https://github.com/kaladron/galactic-bloodshed/blob/master/data/ship.dat

package com.zwsi.gblib

import com.zwsi.gblib.GBDebug.gbAssert

class GBShip(val idxtype: Int, val owner: GBRace, var level: Int, var locationuid: Int) {

    // Level: 1 surface, 2 orbit, 3 star, 4 deep space (5 Hyperspace?)

    // Set at creation
    val id: Int
    val uid: Int

    val name: String
    val type: String

    val speed: Int

    init {
        id = GBData.getNextGlobalId()
        owner.universe.allShips.add(this)
        uid = owner.universe.allShips.indexOf(this)
        owner.raceShips.add(this)

        when(level){
            1 -> {
                gbAssert { owner.universe.allPlanets.elementAtOrNull(locationuid) != null}
                owner.universe.allPlanets[locationuid].landedShips.add(this)
            }
            2 -> {
                gbAssert { owner.universe.allPlanets.elementAtOrNull(locationuid) != null}
                owner.universe.allPlanets[locationuid].orbitShips.add(this)
            }
            3 -> {
                gbAssert { owner.universe.allStars.elementAtOrNull(locationuid) != null}
                owner.universe.allStars[locationuid].starShips.add(this)
            }
            4 -> {
                owner.universe.universeShips.add(this)
            }
            else -> {
                gbAssert ("Bad Parameters for ship placement" + level + "." + locationuid, {true==true} )
            }

        }


        type = GBData.getShipType(idxtype)
        name = type + " " + owner.raceShips.indexOf(this)
        speed = GBData.getShipSpeed(idxtype)
    }

    fun moveShip(level: Int, locationuid: Int) {
        GBDebug.l3("Moving Ship"+level+locationuid)
        this.locationuid = locationuid // hardcode planet...
    }

    fun getLocation() : String {
        when(level){
            1 -> {
                return "Surface of " + owner.universe.allPlanets[locationuid].name +
                        " in system " + owner.universe.allStars[locationuid].name
            }
            2 -> {
                return "Orbit of " + owner.universe.allPlanets[locationuid].name +
                        " in system " + owner.universe.allStars[locationuid].name
            }
            3 -> {
                return "System " + owner.universe.allStars[locationuid].name
            }
            4 -> {
                return "Deep Space"
            }
            else -> {return "Limbo"}
        }


    }

    fun getStar() : GBStar? {
        when(level){
            1 -> {
                return GBController.universe.allPlanets[locationuid].star
            }
            2 -> {
                return GBController.universe.allPlanets[locationuid].star
            }
            3 -> {
                return GBController.universe.allStars[locationuid]
            }
            4 -> {
                return null
            }
            else -> {
                gbAssert("Ship in Limbo", { true })
                return null
            }
        }
    }

}

