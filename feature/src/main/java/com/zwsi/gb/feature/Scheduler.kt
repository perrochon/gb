package com.zwsi.gb.gblib

import com.zwsi.gblib.GBUniverse
import java.util.*

class Scheduler {

    var scheduledActions: MutableList<GBUniverse.GBInstruction> = Collections.synchronizedList(arrayListOf<GBUniverse.GBInstruction>())

}