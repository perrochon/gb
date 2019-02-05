// Copyright 2018 Louis Perrochon. All rights reserved

// Mission Logic
//

package com.zwsi.gb.feature

import android.content.Context
import com.zwsi.gblib.GBController
import com.zwsi.gblib.GBData.Companion.FACTORY
import com.zwsi.gblib.GBData.Companion.POD
import com.zwsi.gblib.GBLocation.Companion.LANDED
import com.zwsi.gblib.GBLocation.Companion.ORBIT

object MissionController {

    private var missionStatus = 0; // what is current mission

    fun getCurrentMission(context: Context): String {

        return when (missionStatus) {

            0 -> context.getString(R.string.m1n0)

            1 -> context.getString(R.string.m1n1)
            2 -> context.getString(R.string.m1n2)
            3 -> context.getString(R.string.m1n3)
            4 -> context.getString(R.string.m1n4)
            5 -> context.getString(R.string.m1n5)
            6 -> context.getString(R.string.m1n6)

            else ->
                ""
        }
    }

    fun checkMissionStatus() {
        // Mission Status 0 only shows once and can be longer.
        if (missionStatus == 0) {
            missionStatus++
            return
        }
        if (missionStatus == 1) {
            for (s in GBController.u.getAllShipsList()) {
                if (s.idxtype == FACTORY) {
                    missionStatus++
                    return
                }
            }
        }
        if (missionStatus == 2) {
            for (s in GBController.u.getAllShipsList()) {
                if (s.idxtype == POD) {
                    missionStatus++
                    return
                }
            }
        }
        if (missionStatus == 3) {
            for (s in GBController.u.getAllShipsList()) {
                if (s.loc.level == ORBIT) {
                    missionStatus++
                    return
                }
            }
        }
        if (missionStatus == 4) {
            for (s in GBController.u.getAllShipsList()) {
                if ((s.loc.level == LANDED) && (s.loc.getPlanet()!!.uid > 0)) {
                    missionStatus++
                    return
                }
            }
        }
        if (missionStatus == 5) {
            var success = true
            for (p in GBController.u.allStars[0]!!.starPlanets) {
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