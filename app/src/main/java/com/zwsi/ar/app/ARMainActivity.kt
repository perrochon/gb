package com.zwsi.ar.app


import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.view.View
import com.zwsi.ar.app.ARViewModel.Companion.newsHistory
import com.zwsi.ar.app.ARViewModel.Companion.ready
import com.zwsi.ar.app.ARViewModel.Companion.showContButton
import com.zwsi.ar.app.ARViewModel.Companion.superSensors
import com.zwsi.ar.app.ARViewModel.Companion.uidActivePlayer
import com.zwsi.ar.app.ARViewModel.Companion.vm
import com.zwsi.gblib.GBController
import com.zwsi.gblib.GBData
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class ARMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        // setTheme(R.style.AppTheme) // TODO Switch back from the Launcher Theme, but this won't compile to APK
        // setTheme(R.style.AppTheme) works in Android Studio -> Emulator, but not when building APKs. Error is
        // E:\AndroidStudioProjects\gb\feature\src\main\java\com\zwsi\gb\feature\MainActivity.kt: (25, 20): Unresolved reference: style

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ARViewModel.context = applicationContext
        ARViewModel.updatePrefs()

        GBController.currentFilePath = filesDir // Tell the controller where to save games

        text_version.text=BuildConfig.VERSIONNAME

        text_messageBox.movementMethod = ScrollingMovementMethod()

        button_player1.isEnabled = false
        button_player1.text = "Play"
        button_player1.setOnClickListener {
            uidActivePlayer = 0
            gotoMap(it)
        }

        space_player2.visibility = View.GONE
        button_player2.isEnabled = false
        button_player2.visibility = View.GONE
        button_player2.setOnClickListener {
            uidActivePlayer = 1
            gotoMap(it)
        }

        button_options.setOnClickListener {
            if (!GlobalStuff.doubleClick()) {
                val intent = Intent(this, AROptionsActivity::class.java)
                startActivity(intent)
            }
        }

        button_stats.setOnClickListener {
            if (!GlobalStuff.doubleClick()) {
                val intent = Intent(this, ARPlayerActivity::class.java)
                startActivity(intent)
            }
        }

        button_save.isEnabled = false
        button_save.setOnClickListener {
            // FIXME TODO Implement Save Game
        }

        button_load.setOnClickListener {
            if (!GlobalStuff.doubleClick()) {
                GlobalStuff.autoDo = false
                val intent = Intent(this, ARLoadActivity::class.java)
                startActivity(intent)
            }
        }

        button_do.setOnClickListener {
            GlobalStuff.doUniverse(it)
        }

        if (showContButton) {
            button_continuous.visibility = View.VISIBLE
            space_continuous.visibility = View.VISIBLE
        } else {
            button_continuous.visibility = View.GONE
            space_continuous.visibility = View.GONE
        }
        button_continuous.setOnClickListener {
            GlobalStuff.toggleContinuous(it)
        }

        button_help.setOnClickListener {
            if (!GlobalStuff.doubleClick()) {
                val intent = Intent(this, ARHelpActivity::class.java)
                startActivity(intent)
            }
        }

        val turnObserver = Observer<Int> { newTurn ->
            // TODO This is a bit overkill, as it enables on every new turn
            button_player1.isEnabled = true
            button_player2.isEnabled = true
            text_messageBox.text = ""
            if (superSensors || !vm.secondPlayer) {
                for (article in newsHistory) {
                    text_messageBox.append(article)
                }
            }
            text_messageBox.append("\nTurn: ${newTurn.toString()}\n")
            text_messageBox.invalidate()

            if (vm.secondPlayer) {
                button_player2.visibility = View.VISIBLE
                space_player2.visibility = View.VISIBLE
                button_player1.text = "Player 1"
            } else {
                button_player2.visibility = View.GONE
                space_player2.visibility = View.GONE
                button_player1.text = "Play"
            }

        }  // TODO why is newTurn nullable?
        ARViewModel.currentTurn.observe(this, turnObserver)

        val actionObserver = Observer<Int> { _ ->
            if (showContButton) {
                button_continuous.visibility = View.VISIBLE
                space_continuous.visibility = View.VISIBLE
            } else {
                button_continuous.visibility = View.GONE
                space_continuous.visibility = View.GONE
            }
        }
        ARViewModel.actionsTaken.observe(this, actionObserver)

        // Get a head start on bitmap loading
        if (!ARBitmaps.ready) {
            Thread(Runnable {
                ARBitmaps.loadBitmaps(this)
            }).start()
        }

        // Kick that off last, we want the app up and running asap
        if (filesDir.isDirectory) {
            val current = File(filesDir, GBData.currentGameFileName)
            if (current.exists()) {
                text_messageBox.append("Found current game")
                val json = current.readText()
                if (json != "") {
                    text_messageBox.append(" and attempting to load it.\n\n")
                    Thread(Runnable {
                        GBController.loadUniverseFromJSON(json)
                        GlobalStuff.processGameInfo(json, true)
                    }).start()
                } else {
                    text_messageBox.append(", but file is empty")
                }
            } else {
                text_messageBox.append("Couldn't find any current game. Click LOAD GAME to load a mission or HELP to learn more.")
            }
        }
    }

    public override fun onResume() {  // After a pause OR at startup
        super.onResume()

        if (ready && vm.secondPlayer) {
            button_player2.visibility = View.VISIBLE
            space_player2.visibility = View.VISIBLE
            button_player1.text = "Player 1"
        } else {
            button_player2.visibility = View.GONE
            space_player2.visibility = View.GONE
            button_player1.text = "Play"
        }
    }

    /** Called when the user taps the Map, or the Player 1/Player 2 button */
    private fun gotoMap(@Suppress("UNUSED_PARAMETER") view: View) {
        if (GlobalStuff.doubleClick()) return
        val intent = Intent(this, ARMapActivity::class.java)
        startActivity(intent)
    }

}
