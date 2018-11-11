
package com.zwsi.gblib;

public class GBUniverse {

    // All these variables are package private, because (for now?) we trust the package
    GBData data;
    GBStar[] starsArray; // the star Systems //
    int numberOfStars; // how many star Systems in the Universe
    GBRace[] racesArray; // the star Systems //
    int numberOfRaces; // how many star Systems in the Universe

    GBUniverse(int numberOfStars, int numberOfRaces) {

        this.numberOfStars = numberOfStars;
        starsArray = new GBStar[numberOfStars];
        this.numberOfRaces = numberOfRaces;
        racesArray = new GBRace[numberOfRaces];

        data = new GBData();

        // Place Stars
        GBDebug.l3("Making Stars");
        makeStars();

        makeRaces();

        GBDebug.l3("Universe made");
    }

    void consoleDraw() {
        System.out.println("=============================================");
        System.out.println("The Universe");
        System.out.println("=============================================");
        System.out.println("The Universe contains " + numberOfStars + " star(s).\n");

        for (GBStar i : starsArray) {
            if (i != null) {
                i.consoleDraw(data);
            }
        }
        System.out.println("The Universe contains " + numberOfRaces + " race(s).\n");

        for (GBRace i : racesArray) {
            if (i != null) {
                i.consoleDraw(data);
            }
        }

    }

    private void makeStars() {
        GBDebug.l3("Making Stars");
        for (int i = 0; i < numberOfStars; i++) {
            starsArray[i] = new GBStar(data);
        }

    }

    private void makeRaces() {
        GBDebug.l3("Making Races");

        // Temporary hack

        GBSector sector;
        racesArray[0]= new GBRace(1,"Xenos", 50, 20);
        sector = starsArray[0].planetsArray[0].sectors[0][0];
        sector.population = 100;
        sector.owner = racesArray[0];

        if (numberOfStars > 1) {
            racesArray[1] = new GBRace(2,"Aliens", 100, 10);
            sector = starsArray[1].planetsArray[0].sectors[0][0];
            sector.population = 100;
            sector.owner = racesArray[1];
        }

    }

    void doUniverse() {

        starsArray[0].planetsArray[0].doPlanet();
        starsArray[1].planetsArray[0].doPlanet();
    }

}
