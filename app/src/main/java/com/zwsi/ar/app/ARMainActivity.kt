package com.zwsi.ar.app


import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.text.HtmlCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.method.LinkMovementMethod
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.Space
import android.widget.TextView
import com.zwsi.ar.app.ARViewModel.Companion.newsHistory
import com.zwsi.ar.app.ARViewModel.Companion.ready
import com.zwsi.ar.app.ARViewModel.Companion.showContButton
import com.zwsi.ar.app.ARViewModel.Companion.superSensors
import com.zwsi.ar.app.ARViewModel.Companion.uidActivePlayer
import com.zwsi.ar.app.ARViewModel.Companion.vm
import com.zwsi.gblib.GBController
import com.zwsi.gblib.GBData
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

        // Version TextView
        val version = findViewById<TextView>(R.id.version)
        version.setText(BuildConfig.VERSIONNAME)

        // Set up the MessageBox View to listen to news
        val messageBox: TextView = findViewById<TextView>(R.id.messageBox)!!
        messageBox.movementMethod = ScrollingMovementMethod();
        messageBox.setText("Welcome to Andromeda Rising!\nA game of galactic domination.\n\n")

        val play1Button: Button = findViewById(R.id.Play1Button)
        play1Button.isEnabled = false
        play1Button.text = "Play"
        play1Button.setOnClickListener(View.OnClickListener {
            uidActivePlayer = 0
            gotoMap(it)
        })

        val play2Button: Button = findViewById(R.id.Play2Button)
        play2Button.isEnabled = false
        val play2Space: Space = findViewById(R.id.Play2Space)
        play2Button.visibility = View.GONE
        play2Space.visibility = View.GONE
        play2Button.setOnClickListener(View.OnClickListener {
            uidActivePlayer = 1
            gotoMap(it)
        })

        val optionsButton: Button = findViewById(R.id.OptionsButton)
        optionsButton.setOnClickListener(View.OnClickListener {
            if (!GlobalStuff.doubleClick()) {
                val intent = Intent(this, AROptionsActivity::class.java)
                startActivity(intent)
            }
        })

        val statsButton: Button = findViewById(R.id.StatsButton)
        statsButton.setOnClickListener(View.OnClickListener {
            if (!GlobalStuff.doubleClick()) {
                val intent = Intent(this, ARPlayerActivity::class.java)
                startActivity(intent)
            }
        })

        val saveButton: Button = findViewById(R.id.SaveButton)
        saveButton.isEnabled = false
        saveButton.setOnClickListener(View.OnClickListener {
        })

        val loadButton: Button = findViewById(R.id.LoadButton)
        loadButton.setOnClickListener(View.OnClickListener {
            if (!GlobalStuff.doubleClick()) {
                GlobalStuff.autoDo = false
                val intent = Intent(this, ARLoadActivity::class.java)
                startActivity(intent)
            }
        })

        val doButton: Button = findViewById(R.id.DoButton)
        doButton.setOnClickListener(View.OnClickListener {
            GlobalStuff.doUniverse(it)
        })

        val contButton: Button = findViewById(R.id.ContinuousButton)
        val contSpace: Space = findViewById(R.id.ContinuousSpace)
        if (showContButton) {
            contButton.visibility = View.VISIBLE
            contSpace.visibility = View.VISIBLE
        } else {
            contButton.visibility = View.GONE
            contSpace.visibility = View.GONE
        }
        contButton.setOnClickListener(View.OnClickListener {
            GlobalStuff.toggleContinuous(it)
        })

        val helpButton: Button = findViewById(R.id.HelpButtonMain)
        helpButton.setOnClickListener(View.OnClickListener {
            if (!GlobalStuff.doubleClick()) {
                val intent = Intent(this, ARHelpActivity::class.java)
                startActivity(intent)
            }
        })

        val turnObserver = Observer<Int> { newTurn ->
            // TODO This is a bit overkill, as it enables on every new turn
            play1Button.isEnabled = true
            play2Button.isEnabled = true
            messageBox.text = ""
            if (superSensors || !vm.secondPlayer) {
                for (article in newsHistory) {
                    messageBox.append(article)
                }
            }
            messageBox.append("\nTurn: ${newTurn.toString()}\n")
            messageBox.invalidate()

            if (vm.secondPlayer) {
                play2Button.visibility = View.VISIBLE
                play2Space.visibility = View.VISIBLE
                play1Button.text = "Player 1"
            } else {
                play2Button.visibility = View.GONE
                play2Space.visibility = View.GONE
                play1Button.text = "Play"
            }

        }  // TODO why is newTurn nullable?
        ARViewModel.currentTurn.observe(this, turnObserver)

        val actionObserver = Observer<Int> { _ ->
            if (showContButton) {
                contButton.visibility = View.VISIBLE
                contSpace.visibility = View.VISIBLE
            } else {
                contButton.visibility = View.GONE
                contSpace.visibility = View.GONE
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
                messageBox.append("Found current game")
                val json = current.readText()
                if (json != "") {
                    messageBox.append(" and attempting to load it.\n\n")
                    Thread(Runnable {
                        GBController.loadUniverseFromJSON(json)
                        GlobalStuff.processGameInfo(json, true)
                    }).start()
                } else {
                    messageBox.append(", but file is empty")
                }
            } else {
                messageBox.append("Couldn't find any current game. Click LOAD GAME to load a mission or HELP to learn more.")
            }
        }
    }

    public override fun onResume() {  // After a pause OR at startup
        super.onResume()

        val play1Button: Button = findViewById(R.id.Play1Button)
        val play2Button: Button = findViewById(R.id.Play2Button)
        val play2Space: Space = findViewById(R.id.Play2Space)

        if (ready && vm.secondPlayer) {
            play2Button.visibility = View.VISIBLE
            play2Space.visibility = View.VISIBLE
            play1Button.text = "Player 1"
        } else {
            play2Button.visibility = View.GONE
            play2Space.visibility = View.GONE
            play1Button.text = "Play"
        }
    }

    /** Called when the user taps the Map, or the Player 1/Player 2 button */
    private fun gotoMap(@Suppress("UNUSED_PARAMETER") view: View) {
        if (GlobalStuff.doubleClick()) return
        val intent = Intent(this, ARMapActivity::class.java)
        startActivity(intent)
    }

}
