package com.zwsi.ar.app

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.zwsi.gblib.GBController
import com.zwsi.gblib.GBData
import kotlinx.android.synthetic.main.activity_load.*
import java.io.File

class ARLoadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load)

        text_version.text=BuildConfig.VERSIONNAME

        var lastSelection = 0 // FIXME: Use resource IDs, and tag the button...
        val buttons = arrayListOf<Button>()

        // TODO Reduce the code duplication below
        buttons.add(button_1_random)
        button_1_random.setOnClickListener {
            lastSelection = 10
            GlobalStuff.handleClickInSelectionActivity(it, buttons)
        }

        buttons.add(button_2_random)
        button_2_random.setOnClickListener {
            lastSelection = 20
            GlobalStuff.handleClickInSelectionActivity(it, buttons)
        }

        buttons.add(button_1_1)
        button_1_1.setOnClickListener {
            lastSelection = 11
            GlobalStuff.handleClickInSelectionActivity(it, buttons)
        }

        buttons.add(button_1_2)
        button_1_2.setOnClickListener {
            lastSelection = 12
            GlobalStuff.handleClickInSelectionActivity(it, buttons)
        }

        buttons.add(button_1_3)
        button_1_3.setOnClickListener {
            lastSelection = 13
            GlobalStuff.handleClickInSelectionActivity(it, buttons)
        }

        buttons.add(button_1_4)
        button_1_4.setOnClickListener {
            lastSelection = 14
            GlobalStuff.handleClickInSelectionActivity(it, buttons)
        }

        buttons.add(button_1_5)
        button_1_5.setOnClickListener {
            lastSelection = 15
            GlobalStuff.handleClickInSelectionActivity(it, buttons)
        }

        buttons.add(button_1_6)
        button_1_6.setOnClickListener {
            lastSelection = 16
            GlobalStuff.handleClickInSelectionActivity(it, buttons)
        }

        buttons.add(button_1_demo)
        button_1_demo.setOnClickListener {
            lastSelection = 31
            GlobalStuff.handleClickInSelectionActivity(it, buttons)
        }

        buttons.add(button_2_1)
        button_2_1.setOnClickListener {
            lastSelection = 21
            GlobalStuff.handleClickInSelectionActivity(it, buttons)
        }

        buttons.add(button_2_2)
        button_2_2.setOnClickListener {
            lastSelection = 22
            GlobalStuff.handleClickInSelectionActivity(it, buttons)
        }

        buttons.add(button_2_3)
        button_2_3.setOnClickListener {
            lastSelection = 23
            GlobalStuff.handleClickInSelectionActivity(it, buttons)
        }

        button_help.setOnClickListener {
            if (!GlobalStuff.doubleClick()) {
                val intent = Intent(this, ARHelpActivity::class.java)
                val b = Bundle()
                b.putString("url", "file:///android_asset/loadHelp.html")
                intent.putExtras(b)
                startActivity(intent)
            }
        }


        button_done.setOnClickListener {

            when (lastSelection) {
                10 -> makeUniverse(it, false)
                20 -> makeUniverse(it, true)
                in 11..16, in 21..23, 31 -> loadUniverse(it, lastSelection)
            }
            finish()
        }

        button_cancel.setOnClickListener {
            finish()
        }
    }

    fun makeUniverse(@Suppress("UNUSED_PARAMETER") view: View, secondPlayer: Boolean) {
        if (SystemClock.elapsedRealtime() - GlobalStuff.lastClickTime < GlobalStuff.clickDelay) {
            return
        }
        GlobalStuff.lastClickTime = SystemClock.elapsedRealtime()

        Toast.makeText(view.context, "Creating a new Universe", Toast.LENGTH_SHORT).show()

        Thread(Runnable {
            val json = GBController.makeAndSaveUniverse(secondPlayer)
            GlobalStuff.processGameInfo(json, true)
        }).start()
    }

    fun loadUniverse(view: View, number: Int) {
        if (SystemClock.elapsedRealtime() - GlobalStuff.lastClickTime < GlobalStuff.clickDelay) {
            return
        }
        GlobalStuff.lastClickTime = SystemClock.elapsedRealtime()

        Toast.makeText(view.context, "Loading Universe $number", Toast.LENGTH_SHORT).show()

        val json = when (number) {
            11 -> view.context.resources.openRawResource(R.raw.mission1).reader().readText()
            12 -> view.context.resources.openRawResource(R.raw.mission2).reader().readText()
            13 -> view.context.resources.openRawResource(R.raw.mission3).reader().readText()
            14 -> view.context.resources.openRawResource(R.raw.mission4).reader().readText()
            15 -> view.context.resources.openRawResource(R.raw.mission5).reader().readText()
            16 -> view.context.resources.openRawResource(R.raw.mission6).reader().readText()
            21 -> view.context.resources.openRawResource(R.raw.map1).reader().readText()
            22 -> view.context.resources.openRawResource(R.raw.map2).reader().readText()
            23 -> view.context.resources.openRawResource(R.raw.map3).reader().readText()
            31 -> view.context.resources.openRawResource(R.raw.demo1).reader().readText()
            else -> File(view.context.filesDir, GBData.currentGameFileName).readText()
        }

        Thread(Runnable {
            GBController.loadUniverseFromJSON(json)  // SERVER Talk to not-remote server
            GlobalStuff.processGameInfo(json, true)
            // todo refresh main activity, because number of players may have changed
            // But Not needed as long as we set secondPlayer from the LoadActivity
        }).start()
    }



}
