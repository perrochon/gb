package com.zwsi.ar.app

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import com.zwsi.gblib.GBUniverse
import kotlin.system.measureNanoTime


class ARViewModel {

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
        var newsHistory = mutableListOf<String>()

        var context: Context? = null // Keeping the Application Context in a singleton in case we need it.
        var showStats = false
        var showRaceStats = true
        var showClickTargets = false
        var showContButton = false
        var superSensors = false

//        var missionResultsString = "999,-1,-1,-1,-1,-1" // Turn when mission success was achieved
//        var missionResults = listOf(0)

        var uidActivePlayer = 0
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
                if (freshUniverse) {
                    newsHistory.clear()
                }
                newsHistory.addAll(vm.news)
                while (newsHistory.size > 50) {
                    newsHistory.removeAt(0)
                }

                if (vm.missionCompletedTurns > 0 ) {
                    val sharedPref = context?.getSharedPreferences("playerstats", Context.MODE_PRIVATE)
                    if (sharedPref != null && vm.missionCompletedTurns < sharedPref.getInt(vm.id, 99999)) {
                        with(sharedPref.edit()) {
                            putInt(vm.id, vm.missionCompletedTurns)
                            apply()
                        }
                    }
                }

                ready = true

            }

            /* FIXME
            Note: You must call the setValue(T) method to update the LiveData object from the main thread.
            If the code is executed in a worker thread, you can use the postValue(T) method instead
            to update the LiveData object.
             */
            // update() should run in the UI thread, so use setValue()
            currentTurn.value = gameinfo.turn

        }

        fun updatePrefs() {
            if (context != null) {
                val sharedPref = context!!.getSharedPreferences("options", Context.MODE_PRIVATE)
                showStats = sharedPref!!.getBoolean("showStats", false)
                showRaceStats = sharedPref.getBoolean("showRaceStats", false)
                showClickTargets = sharedPref.getBoolean("showClickTargets", false)
                showContButton = sharedPref.getBoolean("showContButton", false)
                superSensors = sharedPref.getBoolean("superSensors", false)
            }

        }

    }

}