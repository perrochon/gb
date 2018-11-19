package com.zwsi.gb.feature

//import android.content.Intent
//import android.graphics.Bitmap
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.zwsi.gblib.GBController
import com.zwsi.gblib.GBUniverse
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

        var version = findViewById<TextView>(R.id.version)
        version.setText("0.0.0.97") // for now: 0.0.0.~ #commits...


        if (GBController.universe == null) {

            Thread(Runnable {

                GBController.makeUniverse()

            }).start()

        }

    }


    /** Called when the user taps the Create button */
    fun sendCreate(view: View) {

        output.setText("")

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
    fun sendDo(view: View) {

        output.setText("")

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

    /** Called when the user taps the Planets button */
    fun sendPlanets(view: View) {
        val intent = Intent(this, PlanetsActivity::class.java)
        startActivity(intent)
    }

    /** Called when the user taps the Stars button */
    fun sendStars(view: View) {
        val intent = Intent(this, StarsActivity::class.java)
        startActivity(intent)
    }

    /** Called when the user taps the Races button */
    fun sendXenos(view: View) {
        val intent = Intent(this, RaceActivity::class.java)
        intent.putExtra("race", 0)
        startActivity(intent)
    }

    /** Called when the user taps the Races button */
    fun sendImpi(view: View) {
        val intent = Intent(this, RaceActivity::class.java)
        intent.putExtra("race", 1)
        startActivity(intent)
    }

    /** Called when the user taps the Races button */
    fun races(view: View) {
        val intent = Intent(this, RaceSlide::class.java)
        startActivity(intent)
    }

//    /** Called when the user taps the X Home button */
//    fun sendXHome(view: View) {
//        val intent = Intent(this, PlanetActivity::class.java)
//        intent.putExtra("star", 0)
//        intent.putExtra("planet", 0)
//        startActivity(intent)
//    }
//    /** Called when the user taps the I Home button */
//    fun sendIHome(view: View) {
//        val intent = Intent(this, PlanetActivity::class.java)
//        intent.putExtra("star", 1)
//        intent.putExtra("planet", 0)
//        startActivity(intent)
//    }


}
