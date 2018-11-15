// Copyright 2018 Louis Perrochon. All rights reserved

// Deals with Sectors

package com.zwsi.gblib

class GBSector constructor() {

    var type = -1        // nonexisting type

    internal var type_symbol = "?"
        get() = GBData.sectorTypeConsoleFromIdx(type)

    var population = 0

    private var owner: GBRace? = null
    private var ownerID = -1
    private var ownerName = ""

    fun setOwner(r: GBRace?) {
        owner = r
        ownerID = r!!.uid
        ownerName = r!!.name
    }
    fun getOwner() : GBRace? {return owner}


    internal fun consoleDraw(): String {

        return if (population == 0) {
            " $type_symbol "
        } else {
            //return " \u001B[7m $type_symbol \u001B[m ";
            "[$type_symbol]"
        }
    }

}

