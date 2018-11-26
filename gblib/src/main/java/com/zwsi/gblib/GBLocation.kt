// Copyright 2018 Louis Perrochon. All rights reserved
//
// // Location. Can be planet surface, planet orbit, system space, or deep space. All have different coordinates, etc.

package com.zwsi.gblib

import com.zwsi.gblib.GBDebug.gbAssert

class GBLocation  {

    var level: Int
    var uid: Int
    var x: Int
    var y: Int
    var universe: GBUniverse

    // landed
    constructor(planet: GBPlanet, x: Int, y: Int) {
        this.level = 0
        this.uid = planet.uid
        this.x = x
        this.y = y
        universe = planet.star.universe
    }

    // orbit. x,y not used yet
    constructor(planet: GBPlanet) {
        this.level = 1
        this.uid = planet.uid
        this.x = 0
        this.y = 0
        universe = planet.star.universe
    }

    // system. x,y not used yet
    constructor(star: GBStar, x: Int, y: Int) {
        this.level = 2
        this.uid = star.uid
        this.x = x
        this.y = y
        universe = star.universe
    }

    // landed
    constructor(universe: GBUniverse, x: Int, y: Int) {
        this.level = 3
        this.uid = -1
        this.x = x
        this.y = y
        this.universe = universe
    }

    fun getLocationName() : String {
        when(level){
            1 -> {
                return "Surface of " + universe.allPlanets[uid].name +
                        " in system " + universe.allStars[uid].name
            }
            2 -> {
                return "Orbit of " + universe.allPlanets[uid].name +
                        " in system " + universe.allStars[uid].name
            }
            3 -> {
                return "System " + universe.allStars[uid].name
            }
            4 -> {
                return "Deep Space"
            }
            else -> {
                gbAssert("Ship in Limbo", { true })
                return "Limbo"
            }
        }
    }

    fun getStar() : GBStar? {
        when(level){
            1 -> {
                return GBController.universe.allPlanets[uid].star
            }
            2 -> {
                return GBController.universe.allPlanets[uid].star
            }
            3 -> {
                return GBController.universe.allStars[uid]
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