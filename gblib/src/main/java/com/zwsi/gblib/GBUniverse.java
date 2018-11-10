package com.zwsi.gblib;

public class GBUniverse {

    private int numberOfSystems; // how many star Systems in the Universe
    GBSystem[] systemsArray; // the star Systems // TODO make private an return copy in getter

    GBUniverse(int numberOfSystems) {
        this.numberOfSystems = numberOfSystems;
        systemsArray = new GBSystem[numberOfSystems];
        GBDebug.l1("Universe Created");
    }

    void consoleDraw() {
        System.out.println("============");
        System.out.println("The Universe");
        System.out.println("============");
        System.out.println("The Universe contains " + numberOfSystems + " star system(s).\n");

        for (GBSystem i : systemsArray) {
            if (i != null) {
                i.consoleDraw();
            }
        }
    }

    void randomPopulate() {
        GBDebug.l3("Randomly Populating Universe");

        for (int i = 0; i < systemsArray.length; i++) {
            systemsArray[i] = new GBSystem();
            systemsArray[i].randomPopulate();
        }

    }
}