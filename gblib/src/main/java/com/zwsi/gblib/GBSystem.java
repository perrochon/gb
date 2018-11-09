// Copyright 2018 Louis Perrochon. All rights reserved


package com.zwsi.gblib;

class GBSystem {

    private String name; // name of this system

    private int numberOfPlanets = 2; // how many Planets in this solar Systems
    GBPlanet[] planetsArray = new GBPlanet[numberOfPlanets]; // the solar Systems // TODO make private an return copy in getter


    GBSystem() {
        name = GBData.selectSystemName();
        GBDebug.l3("Created System " + name);

    }

    private String getName() {
        return name;
    }

    void consoleDraw() {
        System.out.println("\n  " + name + " System");
        System.out.println("  " + "====================");
        System.out.println("  The " + name + " system contains " + numberOfPlanets + " planets.");

        for (GBPlanet i : planetsArray) {
            if (i != null) {
                i.consoleDraw();
            }
        }

    }

    void randomPopulate() {
        GBDebug.l3("Populating " + getName());

        for (int i = 0; i < planetsArray.length; i++) {
            planetsArray[i] = new GBPlanet();
        }

    }
}
