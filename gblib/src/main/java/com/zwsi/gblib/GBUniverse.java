
package com.zwsi.gblib;

public class GBUniverse {

    GBData data;
    GBStar[] starsArray; // the star Systems // TODO make private an return copy in getter
    int numberOfStars; // how many star Systems in the Universe

    GBUniverse(int numberOfStars) {

        this.numberOfStars = numberOfStars;
        starsArray = new GBStar[numberOfStars];
        data = new GBData();

        // Place Stars
        GBDebug.l3("Making Stars");
        makeStars(data);

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
    }

    private void makeStars(GBData data) {
        GBDebug.l3("Making Stars");
        for (int i = 0; i < numberOfStars; i++) {
            starsArray[i] = new GBStar(data);
        }

    }
}