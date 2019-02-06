// Copyright 2018 Louis Perrochon. All rights reserved

// Deals with Sectors

package com.zwsi.gblib

import com.squareup.moshi.JsonClass
import com.zwsi.gblib.GBController.Companion.u
import kotlin.math.max

@JsonClass(generateAdapter = true)
data class GBSector constructor(val uidPlanet: Int) {

    // Fixed Properties: type, typeSymbol, revenue
    var type = -1        // nonexisting type
    internal var typeSymbol = "?"
    internal var revenue = 0
    var maxPopulation = 0


    // TODO: This should be part of the constructor
    fun chooseType(planetIdxType: Int) {
        type = GBData.sectorTypesChance[planetIdxType][GBData.rand.nextInt(10)]
        typeSymbol = GBData.sectorTypeConsoleFromIdx(type)
        revenue = GBData.sectorMoneyFromIdx(type)
        maxPopulation = GBData.sectorMaxPopulationFromIdx(type)
    }

    // TODO only store uid. We don't want to create two objects on restore...
    // Changing Properties: planetOwner, planetPopulation
    var owner: GBRace? = null
        set(r) {
            field = r
            if (r==null){
                ownerID = -1
                ownerName = ""

            } else {
                ownerID = field!!.uid
                ownerName = field!!.name
            }
        }

    var ownerID: Int = -1
    var ownerName = ""


    // planetPopulation
    // TODO BUG fix planetPopulation. Right now it goes in mysterious ways
    // Three ways populations can change:
    // 1. Adjusting: Changing planetPopulation in just one sector (setup, landing, killing)
    // 2. Moving: Moving from one sector to another.
    // 3. Growth: (reproduction, and reduction due to incompatibility).
    // 4. Kill: Population gets killed by external influence (war)
    // All three call changePopulationOld to change the values. This will eventually need thread safety/proper transactions

    var population = 0


    internal fun consoleDraw(): String {
        if (population == 0) {
            return " $typeSymbol "
        } else {
            //return " \u001B[7m $type_symbol \u001B[m ";
            return " $ownerID "
            //return "[$type_symbol]"

        }
    }
}

