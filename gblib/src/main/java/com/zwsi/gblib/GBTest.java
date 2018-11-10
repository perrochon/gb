// Copyright 2018 Louis Perrochon. All rights reserved

// GBTest runs step by step through a GB scenario using only what's in GBLib.
// It's a big, manual GBLib integration test. Pass criteria are making sure it finishes, and glance over the output.
//
//   TODO: Move GBTest into a test directory

package com.zwsi.gblib;

import java.lang.String;

public class GBTest {

    public static void main(String[] args) {

        System.out.println("Welcome to GB Test");

        GBDebug.l1("Making Universe");
        GBUniverse universe = new GBUniverse(1);

        // universe.consoleDraw();



        GBDebug.l2("Drawing universe (pre intelligent live)");
        universe.consoleDraw();

        //Place people

        GBDebug.l1("Creating Intelligent Live");

        // Temporary hack
        GBRace first = new GBRace("Xenos");
        GBSector firstSector = universe.starsArray[0].planetsArray[0].sectors[0][0];
        firstSector.population = 100;
        firstSector.race = first;

        //let them spread...
        for (int i = 1; i < 5; i++) {
            universe.starsArray[0].planetsArray[0].doPlanet();
            //universe.consoleDraw();
        }

        GBDebug.l1("Drawing Universe with some population (first star, first planet)");
        universe.consoleDraw();

    }
}

class GBDebug {

    private static final int DEBUG_LEVEL = 1;  // 0 quiet, 1 minimal, 2 verbose, 3 everything

    static void l1(String message) {
        if (DEBUG_LEVEL >= 1) {
            System.out.println("-GBDebug: " + message);
        }
    }

    static void l2(String message) {
        if (DEBUG_LEVEL >= 2) {
            System.out.println("--GBDebug: " + message);
        }
    }

    static void l3(String message) {
        if (DEBUG_LEVEL >= 3) {
            System.out.println("---GBDebug: " + message);
        }
    }
}
















