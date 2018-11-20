package com.zwsi.gb.feature

//import android.content.Intent
//import android.graphics.Bitmap
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.zwsi.gblib.GBController
import kotlinx.android.synthetic.main.activity_main.*

// To redirect stdout to the text view
import java.io.ByteArrayOutputStream
//import java.io.IOException
//import java.io.OutputStream
import java.io.PrintStream


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.AppTheme) // Switch back from the Launcher Theme
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val version = findViewById<TextView>(R.id.version)
        version.setText("0.0.0.123") // for now: 0.0.0.~ #commits...


        if (GBController.universe == null) {

            Thread(Runnable {

                GBController.makeUniverse()

            }).start()

        }

    }


    /** Called when the user taps the Create button */
    fun create(view: View) {

        output.setText("")

        val message = "Recreating the Universe"
        Toast.makeText(view.context, message, Toast.LENGTH_LONG).show()

        Thread(Runnable {

            // Capture output from tester in an byte array
            val baos = ByteArrayOutputStream()
            val ps = PrintStream(baos)
            System.setOut(ps)

            GBController.makeUniverse()

            System.out.flush()

            view.post { // This is going to the button's UI thread, which is the same as the ScrollView
                output.append(baos.toString())
            }


        }).start()

    }

    /** Called when the user taps the Do button */
    fun doUniverse(view: View) {

        output.setText("")

        val message = "Running one turn"
        Toast.makeText(view.context, message, Toast.LENGTH_LONG).show()


        Thread(Runnable {

            // Capture output from tester in an byte array
            val baos = ByteArrayOutputStream()
            val ps = PrintStream(baos)
            System.setOut(ps)

            GBController.doUniverse()

            System.out.flush()

            view.post { // This is going to the button's UI thread, which is the same as the ScrollView
                output.append(baos.toString())
            }

        }).start()


    }

    /** Called when the user taps the Stars button */
    fun stars(view: View) {
        val intent = Intent(this, StarsActivity::class.java)
        startActivity(intent)
    }

    /** Called when the user taps the Planets button */
    fun planets(view: View) {
        val intent = Intent(this, PlanetsSlideActivity::class.java)
        startActivity(intent)
    }

    /** Called when the user taps the Races button */
    fun races(view: View) {
        val intent = Intent(this, RacesSlideActivity::class.java)
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
