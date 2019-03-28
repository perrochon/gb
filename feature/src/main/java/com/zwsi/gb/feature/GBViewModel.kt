package com.zwsi.gb.feature

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.SharedPreferences
import com.zwsi.gblib.GBData.Companion.HEADQUARTERS
import com.zwsi.gblib.GBUniverse
import kotlin.system.measureNanoTime


class GBViewModel {

    // Recommended is tying this to the life cycle of one (1) activity, and have different ones for each activity
    // However MapView requires a huge fraction of all the data, and most other views are subsets so it seems
    // wasteful to not share the ViewModel. That's why this is an app scoped singleton.

    // All App classes need to go through ViewModel to access any data. The viewmodel is generated from JSON
    // in the worker thread, and we just switch it out here.

    companion object {

        const val MIN_ACTIONS = -10
        const val MAX_ACTIONS = 20

        // maybe live data will come from GBController or so, and pass it all over, and this will go away?
        val currentTurn by lazy { MutableLiveData<Int>() }

        var timeLastTurn = 0L  // TODO pack timing information in a collection.
        var timeLastToJSON = 0L
        var timeFileWrite = 0L
        var timeLastLoad = 0L
        var timeFromJson = 0L
        var timeModelUpdate = 0L

        var ready = false
        lateinit var vm: GBUniverse

        var context: Context? = null // FIXME what is going on here?
        private var sharedPref: SharedPreferences? = null
        var showStats = false
        var showClickTargets = false
        var superSensors = false

        var missionResultsString = "999,-1,-1,-1,-1,-1" // Turn when mission success was achieved
        var missionResults = listOf<Int>(0)

        var uidActivePlayer = 0;
        val actionsTaken by lazy { MutableLiveData<Int>() }

        var freshUniverse = false


//        var drawTimes = mutableMapOf<String, Long>() // FIXME Cleanup the long list of ints below

        fun update(gameinfo: GBUniverse, update: Long, json: Long, write: Long, load: Long, fromJSON: Long, fresh: Boolean) {

            timeModelUpdate = measureNanoTime {

                vm = gameinfo

                timeLastTurn = update
                timeLastToJSON = json
                timeFileWrite = write
                timeLastLoad = load
                timeFromJson = fromJSON

                freshUniverse = fresh

                ready = true

            }

            // FIXME Mission Achieved Hack

            if (vm.description == "Mission 2: Conquer neighbouring systems") {
                if (vm.ships.filter { it.value.idxtype == HEADQUARTERS }.count() == 1) {

                    val sharedPref = context!!.getSharedPreferences("options", Context.MODE_PRIVATE)

                    // FIXME Move mission check into separate class
                    // FIXME Better Test (make sure player is winning, not Computer)
                    // FIXME Better string updates

                    missionResultsString = "999,${vm.turn},-1,-1,-1,-1" // FIXME This is a terrible hack
                    with(sharedPref.edit()) {
                        putString("missionResults", missionResultsString)
                        apply()
                    }
                    GBViewModel.updatePlayerStats()
                }

            }

            /*
            Note: You must call the setValue(T) method to update the LiveData object from the main thread.
            If the code is executed in a worker thread, you can use the postValue(T) method instead
            to update the LiveData object.
             */
            // update() should run in the UI thread, so use setValue()
            currentTurn.setValue(gameinfo.turn)

        }

        fun updatePrefs() {
            if (context != null) {
                sharedPref = context!!.getSharedPreferences("options", Context.MODE_PRIVATE)
//                secondPlayer = sharedPref!!.getBoolean("secondPlayer", false)
                showStats = sharedPref!!.getBoolean("showStats", false)
                showClickTargets = sharedPref!!.getBoolean("showClickTargets", false)
                superSensors = sharedPref!!.getBoolean("superSensors", false)
            }

        }

        fun updatePlayerStats() {
            if (context != null) {
                sharedPref = context!!.getSharedPreferences("playerstats", Context.MODE_PRIVATE)
                missionResultsString = sharedPref!!.getString("missionResults", missionResultsString)
                missionResults = missionResultsString.split(",").map { it.toInt() }
            }
        }

    }

}