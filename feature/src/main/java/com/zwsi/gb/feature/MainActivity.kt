package com.zwsi.gb.feature


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
import com.zwsi.gb.feature.GBViewModel.Companion.ready
import com.zwsi.gb.feature.GBViewModel.Companion.showContButton
import com.zwsi.gb.feature.GBViewModel.Companion.superSensors
import com.zwsi.gb.feature.GBViewModel.Companion.uidActivePlayer
import com.zwsi.gb.feature.GBViewModel.Companion.vm
import com.zwsi.gblib.GBController
import com.zwsi.gblib.GBData
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        // setTheme(R.style.AppTheme) // TODO Switch back from the Launcher Theme, but this won't compile to APK
        // setTheme(R.style.AppTheme) works in Android Studio -> Emulator, but not when building APKs. Error is
        // E:\AndroidStudioProjects\gb\feature\src\main\java\com\zwsi\gb\feature\MainActivity.kt: (25, 20): Unresolved reference: style

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GBViewModel.context = applicationContext
        GBViewModel.updatePrefs()
        GBViewModel.updatePlayerStats()

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
                val intent = Intent(this, LoadActivity::class.java)
                startActivity(intent)
            }
        })

        val turnObserver = Observer<Int> { newTurn ->
            // TODO This is a bit overkill, as it enables on every new turn
            play1Button.isEnabled = true
            play2Button.isEnabled = true

            if (GBViewModel.freshUniverse) {
                messageBox.text = ""
                messageBox.append("Universe: ")
                messageBox.append(vm.description)
                messageBox.append("\n")
            }

            messageBox.append("\nTurn: ${newTurn.toString()}\n")

            if (superSensors || !vm.secondPlayer) {
                for (article in vm.news) {
                    messageBox.append(article)
                }
                messageBox.invalidate()
            }

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
        GBViewModel.currentTurn.observe(this, turnObserver)

        val doButton: Button = findViewById(R.id.DoButton)
        doButton.setOnClickListener(View.OnClickListener {
            GlobalStuff.doUniverse(it)
        })

        val contButton: Button = findViewById(R.id.ContinuousButton)
        if (showContButton) {
            contButton.visibility = View.VISIBLE
            contButton.setOnClickListener(View.OnClickListener {
                GlobalStuff.toggleContinuous(it)
            })
        }

        val helpButton: Button = findViewById(R.id.HelpButtonMain)
        helpButton.setOnClickListener(View.OnClickListener {
            val helpText = HtmlCompat.fromHtml(getString(R.string.mainhelp), HtmlCompat.FROM_HTML_MODE_LEGACY)
            val helpView = TextView(this)
            helpView.text = helpText
            helpView.setMovementMethod(LinkMovementMethod.getInstance());
            helpView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_light))
            val scroller = ScrollView(this)
            scroller.setPadding(10, 10, 10, 10)
            scroller.addView(helpView)

            val builder = AlertDialog.Builder(this, R.style.TutorialStyle)
            builder.setView(scroller)
                .setNeutralButton("OK", null)
                .show()
        })

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
                messageBox.append("Couldn't find any current game. Please create a new Universe, or start a mission.")
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
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
    }

//    /** Called when the user taps the Map button */
//    fun gotoOptions(@Suppress("UNUSED_PARAMETER") view: View) {
//        if (GlobalStuff.doubleClick()) return
//        val intent = Intent(this, AROptionsActivity::class.java)
//        startActivity(intent)
//    }

//    fun gotoStats(@Suppress("UNUSED_PARAMETER") view: View) {
//        if (GlobalStuff.doubleClick()) return
//        val intent = Intent(this, ARPlayerActivity::class.java)
//        startActivity(intent)
//    }

//    fun gotoLoad(@Suppress("UNUSED_PARAMETER") view: View) {
//        if (GlobalStuff.doubleClick()) return
//        GlobalStuff.autoDo = false
//        val intent = Intent(this, LoadActivity::class.java)
//        startActivity(intent)
//    }

}
