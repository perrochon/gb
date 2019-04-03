package com.zwsi.gb.feature

import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.zwsi.gblib.GBController
import com.zwsi.gblib.GBData
import java.io.File

class LoadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load)

        // Set up the Version View
        val version = findViewById<TextView>(R.id.version)
        version.setText(BuildConfig.VERSIONNAME) // for now: 0.0.0.~ #commits...

        var lastSelection = 0 // FIXME: Use resource IDs, and tag the button...
        val buttons = arrayListOf<Button>()

        // TODO Reduce the code duplication below
        val new1Button: Button = findViewById(R.id.New1Button)
        buttons.add(new1Button)
        new1Button.setOnClickListener(View.OnClickListener {
            lastSelection = 10
            GlobalStuff.handleClickInSelectionActivity(it, buttons)
        })

        val new2Button: Button = findViewById(R.id.New2Button)
        buttons.add(new2Button)
        new2Button.setOnClickListener(View.OnClickListener {
            lastSelection = 20
            GlobalStuff.handleClickInSelectionActivity(it, buttons)
        })

        val load1Button1: Button = findViewById(R.id.Load1Button1)
        buttons.add(load1Button1)
        load1Button1.setOnClickListener(View.OnClickListener {
            lastSelection = 11
            GlobalStuff.handleClickInSelectionActivity(it, buttons)
        })

        val load1Button2: Button = findViewById(R.id.Load1Button2)
        buttons.add(load1Button2)
        load1Button2.setOnClickListener(View.OnClickListener {
            lastSelection = 12
            GlobalStuff.handleClickInSelectionActivity(it, buttons)
        })

        val load1Button3: Button = findViewById(R.id.Load1Button3)
        buttons.add(load1Button3)
        load1Button3.setOnClickListener(View.OnClickListener {
            lastSelection = 13
            GlobalStuff.handleClickInSelectionActivity(it, buttons)
        })

        val load1Button4: Button = findViewById(R.id.Load1Button4)
        buttons.add(load1Button4)
        load1Button4.setOnClickListener(View.OnClickListener {
            lastSelection = 14
            GlobalStuff.handleClickInSelectionActivity(it, buttons)
        })

        val load1Button5: Button = findViewById(R.id.Load1Button5)
        buttons.add(load1Button5)
        load1Button5.setOnClickListener(View.OnClickListener {
            lastSelection = 15
            GlobalStuff.handleClickInSelectionActivity(it, buttons)
        })

        val load1Button6: Button = findViewById(R.id.Load1Button6)
        buttons.add(load1Button6)
        load1Button6.setOnClickListener(View.OnClickListener {
            lastSelection = 16
            GlobalStuff.handleClickInSelectionActivity(it, buttons)
        })

        val load2Button1: Button = findViewById(R.id.Load2Button1)
        buttons.add(load2Button1)
        load2Button1.setOnClickListener(View.OnClickListener {
            lastSelection = 21
            GlobalStuff.handleClickInSelectionActivity(it, buttons)
        })

        val load2Button2: Button = findViewById(R.id.Load2Button2)
        buttons.add(load2Button2)
        load2Button2.setOnClickListener(View.OnClickListener {
            lastSelection = 22
            GlobalStuff.handleClickInSelectionActivity(it, buttons)
        })

        val load2Button3: Button = findViewById(R.id.Load2Button3)
        buttons.add(load2Button3)
        load2Button3.setOnClickListener(View.OnClickListener {
            lastSelection = 23
            GlobalStuff.handleClickInSelectionActivity(it, buttons)
        })

        val doneButton: Button = findViewById(R.id.DoneButton)
        doneButton.setOnClickListener(View.OnClickListener {

            when (lastSelection) {
                10 -> makeUniverse(it, false)
                20 -> makeUniverse(it, true)
                in 11..16, in 21..23 -> loadUniverse(it, lastSelection)
            }
            finish()
        })

        val buttonCancel = findViewById<Button>(R.id.cancelDestination)

        buttonCancel.setOnClickListener(View.OnClickListener {
            finish()
        })
    }

    fun makeUniverse(@Suppress("UNUSED_PARAMETER") view: View, secondPlayer: Boolean) {
        if (SystemClock.elapsedRealtime() - GlobalStuff.lastClickTime < GlobalStuff.clickDelay) {
            return;
        }
        GlobalStuff.lastClickTime = SystemClock.elapsedRealtime();

        Toast.makeText(view.context, "Creating a new Universe", Toast.LENGTH_SHORT).show()

        Thread(Runnable {
            val json = GBController.makeAndSaveUniverse(secondPlayer)
            GlobalStuff.processGameInfo(json, true)
        }).start()
    }

    fun loadUniverse(view: View, number: Int) {
        if (SystemClock.elapsedRealtime() - GlobalStuff.lastClickTime < GlobalStuff.clickDelay) {
            return;
        }
        GlobalStuff.lastClickTime = SystemClock.elapsedRealtime();

        Toast.makeText(view.context, "Loading Universe ${number}", Toast.LENGTH_SHORT).show()

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
