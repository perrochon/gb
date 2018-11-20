// Copyright 2018 Louis Perrochon. All rights reserved
//
// // Inspiration
// https://github.com/kaladron/galactic-bloodshed/blob/master/data/exam.dat
// https://github.com/kaladron/galactic-bloodshed/blob/master/data/ship.dat

package com.zwsi.gblib

class GBShip(val owner: GBRace, val position: GBStar) {

    // Set at creation
    val id: Int
    val uid: Int
    val idxtype: Int // Ship type

    val name: String
    val type: String

    val speed: Int


    init {
        id = GBData.getNextGlobalId()
        owner.universe.allShips.add(this)
        uid = owner.universe.allShips.indexOf(this)
        owner.raceShips.add(this)
        position.starShips.add(this)


        idxtype = 0;
        type = GBData.getShipType(idxtype)
        name = type + " " + owner.raceShips.indexOf(this)
        speed = GBData.getShipSpeed(idxtype)
    }

}

