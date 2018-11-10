// Copyright 2018 Louis Perrochon. All rights reserved

// Deals with Sectors

package com.zwsi.gblib;

// Data
class GBSector {
    // setters and getters. Easy for primitive types and String, but careful with complex objects.
    int type = -1;        // nonexisting type
    int population = 0;   // nobody lives here (yet)
    GBRace race = null;  // Controlling race. we only want object reference to the race, not a copy of the object.

    String consoleDraw(GBData data) {
        if (population == 0) {
            return " " + data.sectorTypeToSymbol(type) + " ";
        } else {
            //return " \u001B[7m" + GBData.sectorTypeToSymbol(type) + "\u001B[m ";
            return " [" + data.sectorTypeToSymbol(type) + "]";
        }
    }

}

