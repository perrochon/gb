package com.zwsi.gb.feature

//import android.content.Intent
//import android.graphics.Bitmap

/*
// To redirect stdout to the text view
//import java.io.IOException
//import java.io.OutputStream
*/
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.zwsi.gb.feature.GBViewModel.Companion.gi
import com.zwsi.gblib.GBController
import com.zwsi.gblib.GBSavedGame
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import kotlin.system.measureNanoTime
import android.arch.lifecycle.Observer

var lastClickTime = 0L
val clickDelay = 300L

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        // setTheme(R.style.AppTheme) // TODO Switch back from the Launcher Theme, but this won't compile to APK
        // setTheme(R.style.AppTheme) works in Android Studio -> Emulator, but not when building APKs. Error is
        // E:\AndroidStudioProjects\gb\feature\src\main\java\com\zwsi\gb\feature\MainActivity.kt: (25, 20): Unresolved reference: style

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val messageBox: TextView = findViewById<TextView>(R.id.messageBox)!!

        val turnObserver = Observer<Int> { newTurn ->
            messageBox.setText("Turn: ${newTurn.toString()}\n")
            for (article in gi.news!!) {
                messageBox.append(article)
            }
            messageBox.invalidate()
        }  // TODO why is newTurn nullable?
        GBViewModel.currentTurn.observe(this, turnObserver)

        val version = findViewById<TextView>(R.id.version)
        version.setText(BuildConfig.VERSIONNAME) // for now: 0.0.0.~ #commits...

        Thread(Runnable {

            val json = GBController.makeUniverse() // Create Universe if we don't have one...

            // This writes to (or not) /data/data/com.zwsi.gb.app/files/CurrentGame.json
            val writeFileTime = measureNanoTime {
                val file = File(this.filesDir, "CurrentGame.json").writeText(json)
            }

            // FIXME. Need to disable all (most) buttons until we do have a Universe!!!!

            // We create gameinfo in the worker thread, not the UI thread
            var gameInfo = GBSavedGame()
            val fromJsonTime = measureNanoTime {
                val moshi = Moshi.Builder().build()
                val jsonAdapter: JsonAdapter<GBSavedGame> = moshi.adapter(GBSavedGame::class.java).indent("  ")
                gameInfo = jsonAdapter.lenient().fromJson(json)!!
            }

            version.post {
                GBViewModel.update(gameInfo, GBController.elapsedTimeLastUpdate, writeFileTime, fromJsonTime)
                // Enable Buttons Here
            }

        }).start()

    }

    /** Called when the user taps the Create button */
    fun create(view: View) {
        if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
            return;
        }
        lastClickTime = SystemClock.elapsedRealtime();


        messageBox.setText("")

        val message = "God Level: Recreating the Universe"
        Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()

        Thread(Runnable {

            // Capture output from tester in an byte array
//            val baos = ByteArrayOutputStream()
//            val ps = PrintStream(baos)
//            System.setOut(ps)

            GBController.makeUniverse()

//            System.out.flush()

            view.post {
                // This is going to the button's UI thread, which is the same as the ScrollView
                // output.append(baos.toString())
            }

            view.post {
                // Worth making a string in this thread and post just result?
                for (s in gi.news!!)
                    messageBox.append(s)

                messageBox.append(MissionController.getCurrentMission(this))

            }

        }).start()

    }

    /** Called when the user taps the Do button */
    fun doUniverse(view: View) {
        GlobalButtonOnClick.doUniverse(view)
    }

    fun continuousDo(view: View) {
        GlobalButtonOnClick.toggleContinuous(view)
    }

    fun makeStuff(view: View) {
        if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
            return;
        }
        lastClickTime = SystemClock.elapsedRealtime();

        val message = "God Mode: Making Test Stuff"
        Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()

        Thread(Runnable {

            GBController.makeStuff()

        }).start()

    }


    /** Called when the user taps the Stars button */
    fun starmap1( @Suppress("UNUSED_PARAMETER")view: View) {
        if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
            return;
        }
        lastClickTime = SystemClock.elapsedRealtime();
        val intent = Intent(this, StarsActivity::class.java)
        startActivity(intent)
    }

    /** Called when the user taps the Stars button */
    fun starmap2( @Suppress("UNUSED_PARAMETER")view: View) {
        if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
            return;
        }
        lastClickTime = SystemClock.elapsedRealtime();
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
    }

    /** Called when the user taps the Stars button */
    fun stars( @Suppress("UNUSED_PARAMETER")view: View) {
        if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
            return;
        }
        lastClickTime = SystemClock.elapsedRealtime();
        val intent = Intent(this, StarsSlideActivity::class.java)
        startActivity(intent)
    }

    /** Called when the user taps the Planets button */
    fun planets1( @Suppress("UNUSED_PARAMETER")view: View) {
        if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
            return;
        }
        lastClickTime = SystemClock.elapsedRealtime();
        val intent = Intent(this, PlanetsScrollActivity::class.java)
        startActivity(intent)
    }

    /** Called when the user taps the Planets button */
    fun planets2( @Suppress("UNUSED_PARAMETER")view: View) {
        if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
            return;
        }
        lastClickTime = SystemClock.elapsedRealtime();
        val intent = Intent(this, PlanetsSlideActivity::class.java)
        startActivity(intent)
    }

    /** Called when the user taps the Races button */
    fun races( @Suppress("UNUSED_PARAMETER")view: View) {
        if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
            return;
        }
        lastClickTime = SystemClock.elapsedRealtime();
        val intent = Intent(this, RacesSlideActivity::class.java)

        intent.putExtra("title", "All Races")
        intent.putExtra("UID", 0)

        startActivity(intent)
    }

    /** Called when the user taps the Ships button */
    fun ships( @Suppress("UNUSED_PARAMETER")view: View) {
        if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
            return;
        }
        lastClickTime = SystemClock.elapsedRealtime();
        val intent = Intent(this, ShipsSlideActivity::class.java)
        startActivity(intent)
    }

}
