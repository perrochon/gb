// Copyright 2018 Louis Perrochon. All rights reserved

// Mission Logic
//

package com.zwsi.gblib

class GBMissionController() {

    private var missionStatus = 0; // what is current mission

    fun getCurrentMission(): String {

        return when (missionStatus) {

            0 ->
                "Commander! My name is Yonininji. I will be helping you with your missions.\n\n" +
                        "Your first mission is to colonize the 5 planets of Jade, your home system. " +
                        "Find Helle, build a factory, then start building pods and send a pod to one " +
                        "of the other planets your home system. \n\n" +
                        "Remember these things take time. So after you gave your orders, you have to press the " +
                        "[DO] button.\n\n" +
                        "One more thing: If you see greyed out buttons, these are God level shortcuts. Ignore them!\n\n"

            1 -> "Your first mission is to colonize the 5 planets of Jade, your home system. " +
                    "Build a factory, start building pods and send a pod to each " +
                    "of the other planets in the system. Land the pods.\n\n"
            2 -> "Congratualations, you finished your first mission. Feel free to continue to exploit the universe.\n\n"

            else ->
                "You have finished all missions. Feel free to continue to exploit the universe.\n\n"
        }
    }

    fun checkMissionStatus() {
        // Mission Status 0 only shows once and can be longer.
        if (missionStatus == 0) {
            missionStatus++
            return
        }
        if (missionStatus == 1) {
            var success = true
            for (p in GBController.universe.allStars[0].starPlanets) {
                if (p.population == 0) {
                    success = false
                }
            }
            if (success == true) {
                missionStatus++
                return
            }
        }
    }

fun getMissionStatus(): Int {
    return missionStatus
}
}