package com.zwsi.gblib

import com.zwsi.gblib.GBController.Companion.universe
import java.util.*

class GBInstruction(var t: Int, var code: () -> Unit?) {}

class GBSchedulier {


    companion object {

        var scheduledActions= arrayListOf<GBInstruction>()

        fun doSchedule() {
            scheduledActions.forEach() { // Outfactor this to GBScheduler
                GBLog.d("Looking at action for turn ${it.t}")
                if ((it.t == universe.turn) || (it.t == -1)) {
                    run { it.code() }
                }
            }
        }

        fun addInstruction(t: Int, code: () -> Unit?) {
            scheduledActions.add(GBInstruction(t, code))
        }

    }
}