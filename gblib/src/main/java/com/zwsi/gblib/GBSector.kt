// Copyright 2018-2019 Louis Perrochon. All rights reserved

// Deals with Sectors

package com.zwsi.gblib

import com.squareup.moshi.JsonClass
import com.zwsi.gblib.GBController.Companion.u

@JsonClass(generateAdapter = true)
data class GBSector constructor(val uidPlanet: Int) {

    // Fixed Properties: type, typeSymbol, revenue
    var type = -1        // nonexisting type
    internal var typeSymbol = "?"
    internal var revenue = 0
    var maxPopulation = 0

    var population = 0
    var uidSectorOwner: Int = -1
    val sectorOwner: GBRace
        get() = u.race(uidSectorOwner)

    // TODO: This should be part of the constructor
    fun chooseType(planetIdxType: Int) {
        type = GBData.sectorTypesChance[planetIdxType][GBData.rand.nextInt(10)]
        typeSymbol = GBData.sectorTypeConsoleFromIdx(type)
        revenue = GBData.sectorMoneyFromIdx(type)
        maxPopulation = GBData.sectorMaxPopulationFromIdx(type)
    }

    internal fun consoleDraw(): String {
        if (population == 0) {
            return " $typeSymbol "
        } else {
            //return " \u001B[7m $type_symbol \u001B[m ";
            return " $uidSectorOwner "
            //return "[$type_symbol]"

        }
    }
}

