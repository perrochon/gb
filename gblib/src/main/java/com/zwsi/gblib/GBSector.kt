// Copyright 2018 Louis Perrochon. All rights reserved

// Deals with Sectors

package com.zwsi.gblib

class GBSector constructor() {

    var type = -1        // nonexisting type

    internal var type_symbol = "?"
        get() = GBData.sectorTypeConsoleFromIdx(type)

    var population = 0

    private var owner: GBRace? = null
    private var ownerID: Int = -1
    private var ownerName = ""

    fun setOwner(r: GBRace?) {
        owner = r
        ownerID = r!!.uId
        ownerName = r!!.name
    }

    fun growPopulation() {
        population = population *  (100 + getBirthrate()) / 100
    }

    fun getBirthrate() : Int {
        return owner?.birthrate ?: 0
    }
    fun getOwner() : GBRace? {return owner}

    internal fun consoleDraw(): String {

        if (population == 0) {
            return " $type_symbol "
        } else {
            //return " \u001B[7m $type_symbol \u001B[m ";
            return " $ownerID "
            return "[$type_symbol]"

        }
    }

}

