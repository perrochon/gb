// Copyright 2018 Louis Perrochon. All rights reserved

// GBStar deals with anything on the star system wide level


package com.zwsi.gblib;

class GBStar {

    private String name; // name of this system

    private int numberOfPlanets = 2; // how many Planets in this solar Systems

    GBPlanet[] planetsArray = new GBPlanet[numberOfPlanets]; // the solar Systems // TODO make private an return copy in getter

    GBStar(GBData data) {
        name = data.selectSystemName();
        makePlanets(data);

        GBDebug.l3("Made System " + name);

    }

    private String getName() {
        return name;
    }

    void consoleDraw(GBData data) {
        System.out.println("\n  " + "====================");
        System.out.println("  " + name + " System");
        System.out.println("  " + "====================");
        System.out.println("  The " + name + " system contains " + numberOfPlanets + " planet(s).");

        for (GBPlanet i : planetsArray) {
            if (i != null) {
                i.consoleDraw(data);
            }
        }

    }

    private void makePlanets(GBData data) {
        GBDebug.l3("Making Planets for star " + name);

        for (int i = 0; i < planetsArray.length; i++) {
            planetsArray[i] = new GBPlanet(data);
        }

    }
}
