package com.zwsi.gblib

import com.zwsi.gblib.GBController.Companion.u

class GBInstruction(var t: Int, var code: () -> Unit?) {}

class GBScheduler {


    companion object {

        var scheduledActions= arrayListOf<GBInstruction>()

        fun doSchedule() {
            scheduledActions.forEach() { // Outfactor this to GBScheduler
                GBLog.d("Looking at action for turn ${it.t}")
                if (
                    (it.t == u.turn) ||
                    (it.t == -1) ||
                    ((it.t < 0) && ((u.turn % -it.t) == 0))
                ) {
                    run { it.code() }
                }
            }
            scheduledActions.removeIf { it.t == u.turn }
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