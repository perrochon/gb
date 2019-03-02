package com.zwsi.gb.feature


import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.zwsi.gb.feature.GBViewModel.Companion.vm
import com.zwsi.gblib.GBController
import com.zwsi.gblib.GBData
import java.io.File
import android.webkit.WebView




var lastClickTime = 0L
val clickDelay = 300L

class MainActivity : AppCompatActivity() {

    var helpText : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        // setTheme(R.style.AppTheme) // TODO Switch back from the Launcher Theme, but this won't compile to APK
        // setTheme(R.style.AppTheme) works in Android Studio -> Emulator, but not when building APKs. Error is
        // E:\AndroidStudioProjects\gb\feature\src\main\java\com\zwsi\gb\feature\MainActivity.kt: (25, 20): Unresolved reference: style

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GBController.currentFilePath = filesDir // Tell the controller where to save games

        // Set up the Version View
        val version = findViewById<TextView>(R.id.version)
        version.setText(BuildConfig.VERSIONNAME) // for now: 0.0.0.~ #commits...

        // Set up the MessageBox View to listen to news
        val messageBox: TextView = findViewById<TextView>(R.id.messageBox)!!
        messageBox.setText("Welcome to Andromeda Rising!\nA game of galactic domination.\n\n")

        val playButton: Button = findViewById(R.id.PlayButton)
        playButton.setEnabled(false)
        playButton.setOnClickListener(View.OnClickListener {
            mapView(it)
        })

        val turnObserver = Observer<Int> { newTurn ->
            // TODO This is a bit overkill, as it enables on every new turn
            playButton.setEnabled(true)
            messageBox.append("\nTurn: ${newTurn.toString()}\n")
            for (article in vm.news) {
                messageBox.append(article)
            }
            messageBox.invalidate()
        }  // TODO why is newTurn nullable?
        GBViewModel.currentTurn.observe(this, turnObserver)

        val doButton: Button = findViewById(R.id.DoButton)
        doButton.setOnClickListener(View.OnClickListener {
            GlobalStuff.doUniverse(it)
        })

        val contButton: Button = findViewById(R.id.ContinuousButton)
        contButton.setOnClickListener(View.OnClickListener {
            GlobalStuff.toggleContinuous(it)
        })

        val newButton: Button = findViewById(R.id.NewButton)
        newButton.setOnClickListener(View.OnClickListener {
            GlobalStuff.makeUniverse(it)
            messageBox.append("\nCreated New Universe.\n")
        })

        // Pre-load HTML to make later clicks on Help faster
        // TODO: Not sure this is needed. It didn't speed up on the emulator, and no big difference on Pixel 3
        Thread(Runnable {
            helpText = getString(R.string.tutorial)
        }).start()

        val helpButton: Button = findViewById(R.id.HelpButton)
        helpButton.setOnClickListener(View.OnClickListener {
            val helpView = WebView(this)
            helpView.setBackgroundColor(Color.BLACK)
            val builder = AlertDialog.Builder(this, R.style.TutorialStyle)

            if (helpText != null) {
                helpView.loadData(helpText, "text/html", "utf-8")
                builder.setView(helpView)
                    .setNeutralButton("OK", null)
                    .show()
            }
        })

        val loadButton1: Button = findViewById(R.id.LoadButton1)
        loadButton1.setOnClickListener(View.OnClickListener {
            GlobalStuff.loadUniverse(it, 1)
            messageBox.append("\nLoaded Mission 1.\n")
        })

        val loadButton2: Button = findViewById(R.id.LoadButton2)
        loadButton2.setOnClickListener(View.OnClickListener {
            GlobalStuff.loadUniverse(it, 2)
            messageBox.append("\nLoaded Mission 2.\n")
        })

        val loadButton3: Button = findViewById(R.id.LoadButton3)
        loadButton3.setOnClickListener(View.OnClickListener {
            GlobalStuff.loadUniverse(it, 3)
            messageBox.append("\nLoaded Mission 3.\n")
        })

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
                        GlobalStuff.processGameInfo(json)
                    }).start()
                } else {
                    messageBox.append(", but file is empty")
                }
            } else {
                messageBox.append("Couldn't find any current game. Please creat a new Universe, or start a mission.")
            }
        }
    }

    /** Called when the user taps the Map button */
    fun mapView(@Suppress("UNUSED_PARAMETER") view: View) {
        if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
            return;
        }
        lastClickTime = SystemClock.elapsedRealtime();
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
    }

    // TODO DELETE unless we need it again
    fun makeStuff(view: View) {
        if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
            return;
        }
        lastClickTime = SystemClock.elapsedRealtime();

        val message = "God Mode: Not doing anything right now"
        Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()

    }


    // TODO DELETE The stuff below is all deprecated

//    /** Called when the user taps the Stars button */
//    fun starmap1( @Suppress("UNUSED_PARAMETER")view: View) {
//        if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
//            return;
//        }
//        lastClickTime = SystemClock.elapsedRealtime();
//        val intent = Intent(this, StarsActivity::class.java)
//        startActivity(intent)
//    }
//
//
//    /** Called when the user taps the Stars button */
//    fun stars( @Suppress("UNUSED_PARAMETER")view: View) {
//        if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
//            return;
//        }
//        lastClickTime = SystemClock.elapsedRealtime();
//        val intent = Intent(this, StarsSlideActivity::class.java)
//        startActivity(intent)
//    }
//
//    /** Called when the user taps the Planets button */
//    fun planets1( @Suppress("UNUSED_PARAMETER")view: View) {
//        if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
//            return;
//        }
//        lastClickTime = SystemClock.elapsedRealtime();
//        val intent = Intent(this, PlanetsScrollActivity::class.java)
//        startActivity(intent)
//    }
//
//    /** Called when the user taps the Planets button */
//    fun planets2( @Suppress("UNUSED_PARAMETER")view: View) {
//        if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
//            return;
//        }
//        lastClickTime = SystemClock.elapsedRealtime();
//        val intent = Intent(this, PlanetsSlideActivity::class.java)
//        startActivity(intent)
//    }
//
//    /** Called when the user taps the Races button */
//    fun races( @Suppress("UNUSED_PARAMETER")view: View) {
//        if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
//            return;
//        }
//        lastClickTime = SystemClock.elapsedRealtime();
//        val intent = Intent(this, RacesSlideActivity::class.java)
//
//        intent.putExtra("title", "All Races")
//        intent.putExtra("UID", 0)
//
//        startActivity(intent)
//    }
//
//    /** Called when the user taps the Ships button */
//    fun ships( @Suppress("UNUSED_PARAMETER")view: View) {
//        if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
//            return;
//        }
//        lastClickTime = SystemClock.elapsedRealtime();
//        val intent = Intent(this, ShipsSlideActivity::class.java)
//        startActivity(intent)
//    }

}
