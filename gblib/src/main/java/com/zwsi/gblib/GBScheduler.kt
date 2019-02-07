// Copyright 2018-2019 Louis Perrochon. All rights reserved

// Schedule code to be run at a later turn. Code is stored in a lambda in GBInstruction

package com.zwsi.gblib

import com.zwsi.gblib.GBController.Companion.u

class GBInstruction(var t: Int, var code: () -> Unit?) {}

// GBScheduler is not persistent. It will not survive a reload.
// This means any client of Scheduler needs to reschedule any actions on restore!
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

        // FIXME These are not persisted, so will not run "at" if the game reloads between "now" and "at"
        // Safer to schedule "always" or "every" and check in the code if it's time to run
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