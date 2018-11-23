// Copyright 2018 Louis Perrochon. All rights reserved

// Mission Logic
//

package com.zwsi.gblib

class GBMissioController() {

    var missionStatus = 0; // what is current mission

    fun getCurrentMission(): String {
        return when (missionStatus) {

            0 ->
                "Commander! Your first mission is to build a factory on your home planet."

            else ->
                "You are out of missions"
        }
    }

}