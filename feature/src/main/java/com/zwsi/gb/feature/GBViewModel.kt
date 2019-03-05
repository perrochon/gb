package com.zwsi.gb.feature

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.SharedPreferences
import com.zwsi.gblib.GBUniverse
import kotlin.system.measureNanoTime


class GBViewModel {

    // Recommended is tying this to the life cycle of one (1) activity, and have different ones for each activity
    // However MapView requires a huge fraction of all the data, and most other views are subsets so it seems
    // wasteful to not share the ViewModel. That's why this is an app scoped singleton.

    // All App classes need to go through ViewModel to access any data. The viewmodel is generated from JSON
    // in the worker thread, and we just switch it out here.

    companion object {

        // maybe live data will come from GBController or so, and pass it all over, and this will go away?
        val currentTurn by lazy { MutableLiveData<Int>() }

        var timeLastTurn = 0L  // TODO pack timing information in a collection.
        var timeLastToJSON = 0L
        var timeFileWrite = 0L
        var timeLastLoad = 0L
        var timeFromJson = 0L
        var timeModelUpdate = 0L
        val raceCanSeeStar = HashMap<Pair<Int, Int>, Boolean>()

        lateinit var vm: GBUniverse

        var context: Context? = null
        var sharedPref: SharedPreferences? = null
        var showStats = false
        var showClickTargets = false
        var superSensors = false


//        var times = mutableMapOf<String, Long>() // FIXME Cleanup the long list of ints below

        fun update(gameinfo: GBUniverse, update: Long, json: Long, write: Long, load: Long, fromJSON: Long) {

            timeModelUpdate = measureNanoTime {

                vm = gameinfo

                timeLastTurn = update
                timeLastToJSON = json
                timeFileWrite = write
                timeLastLoad = load
                timeFromJson = fromJSON

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
                showStats = sharedPref!!.getBoolean("showStats", false)
                showClickTargets = sharedPref!!.getBoolean("showClickTargets", false)
                superSensors = sharedPref!!.getBoolean("superSensors", false)
            }

        }

    }

}