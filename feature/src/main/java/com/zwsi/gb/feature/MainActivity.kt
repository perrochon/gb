package com.zwsi.gb.feature

//import android.content.Intent
//import android.graphics.Bitmap

// To redirect stdout to the text view
//import java.io.IOException
//import java.io.OutputStream
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.zwsi.gblib.GBController
import com.zwsi.gblib.GBUniverse
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.PrintStream


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        //setTheme(R.style.AppTheme) // TODO switch back from the Launcher Theme, but this won't compile to APK
        // setTheme(R.style.AppTheme) works in Android Studio -> Emulator, but not when building APKs. Error is
        // E:\AndroidStudioProjects\gb\feature\src\main\java\com\zwsi\gb\feature\MainActivity.kt: (25, 20): Unresolved reference: style

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val version = findViewById<TextView>(R.id.version)
        version.setText("0.0.0.133") // for now: 0.0.0.~ #commits...


        Thread(Runnable {
            // Need to do this in other thread, as just checking for null will generate the universe
            GBController.universe // Create Universe if we don't have one...
            version.post {
                // Worth making a string in this thread and post just result?
                for (s in GBController.universe.news)
                    output.append(s)
            }
        }).start()


    }


    /** Called when the user taps the Create button */
    fun create(view: View) {

        output.setText("")

        val message = "God Level: Recreating the Universe"
        Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()

        Thread(Runnable {

            // Capture output from tester in an byte array
            val baos = ByteArrayOutputStream()
            val ps = PrintStream(baos)
            System.setOut(ps)

            GBController.makeUniverse()

            System.out.flush()

            view.post {
                // This is going to the button's UI thread, which is the same as the ScrollView
                // output.append(baos.toString())
            }

            view.post {
                // Worth making a string in this thread and post just result?
                for (s in GBController.universe!!.news)
                    output.append(s)
            }

        }).start()

    }

    /** Called when the user taps the Do button */
    fun doUniverse(view: View) {

        output.setText("")

        val message = "Executing Orders"
        Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()

        Thread(Runnable {

            // Capture output from tester in an byte array
//            val baos = ByteArrayOutputStream()
//            val ps = PrintStream(baos)
//            System.setOut(ps)

            GBController.doUniverse()

//            System.out.flush()

//            view.post { // This is going to the button's UI thread, which is the same as the ScrollView
//                // output.append(baos.toString())
//            }

            view.post {
                // Worth making a string in this thread and post just result?
                for (s in GBController.universe!!.news)
                    output.append(s)
            }

        }).start()

    }

    /** Called when the user taps the Stars button */
    fun starmap1(view: View) {
        val intent = Intent(this, StarsActivity::class.java)
        startActivity(intent)
    }

    /** Called when the user taps the Stars button */
    fun starmap2(view: View) {
        Toast.makeText(view.context, "God level command!", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
    }

    /** Called when the user taps the Stars button */
    fun stars(view: View) {
        val intent = Intent(this, StarsSlideActivity::class.java)
        startActivity(intent)
    }

    /** Called when the user taps the Planets button */
    fun planets1(view: View) {
        val intent = Intent(this, PlanetsScrollActivity::class.java)
        startActivity(intent)
    }

    /** Called when the user taps the Planets button */
    fun planets2(view: View) {
        val intent = Intent(this, PlanetsSlideActivity::class.java)
        startActivity(intent)
    }

    /** Called when the user taps the Races button */
    fun races(view: View) {
        val racesUID = ArrayList<Int>()
        val intent = Intent(this, RacesSlideActivity::class.java)

        // temporary proof of concept. We don't need the next few lines to display all Races
        for (r in GBController.universe.allRaces) {
            racesUID.add(r.uid)
        }
        racesUID.removeAt(1)
        intent.putExtra("races", racesUID)
        intent.putExtra("title", "All but on Races")
        intent.putExtra("UID", 2)

        startActivity(intent)
    }

    /** Called when the user taps the Ships button */
    fun ships(view: View) {
        val intent = Intent(this, ShipsSlideActivity::class.java)
        startActivity(intent)
    }

    /* Hide System UI. If we do it in all screens, we lose the back button. If we do it only here, background resizes.
       Need to figure out what to do about this.
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
        )
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }
    */
}
