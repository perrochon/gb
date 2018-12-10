package com.zwsi.gblib

import com.zwsi.gblib.GBController.Companion.universe
import java.util.*

class GBInstruction(var t: Int, var code: () -> Unit?) {}

class GBScheduler {


    companion object {

        var scheduledActions= arrayListOf<GBInstruction>()

        fun doSchedule() {
            scheduledActions.forEach() { // Outfactor this to GBScheduler
                GBLog.d("Looking at action for turn ${it.t}")
                if (
                    (it.t == universe.turn) ||
                    (it.t == -1) ||
                    ((it.t < 0) && ((universe.turn % -it.t) == 0))
                ) {
                    run { it.code() }
                }
            }
        }

        fun addInstructionAt(t: Int, code: () -> Unit?) {
            scheduledActions.add(GBInstruction(t, code))
        }

        fun addInstructionAlways(code: () -> Unit?) {
            scheduledActions.add(GBInstruction(-1, code))
        }

        fun addInstructionEvery(t: Int, code: () -> Unit?) {
            scheduledActions.add(GBInstruction(-t, code))
        }

    }
}