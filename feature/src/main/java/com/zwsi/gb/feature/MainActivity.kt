package com.zwsi.gb.feature

//import android.content.Intent
//import android.graphics.Bitmap
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
//import android.widget.EditText
import android.widget.ImageView
import com.zwsi.gblib.GBTest
import kotlinx.android.synthetic.main.activity_main.*

// To redirect stdout to the text view
import java.io.ByteArrayOutputStream
//import java.io.IOException
//import java.io.OutputStream
import java.io.PrintStream


class MainActivity : AppCompatActivity() {

    var tester: GBTest = GBTest()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val planet = findViewById<ImageView>(R.id.planet)


        val d = BitmapFactory.decodeResource(getResources(), R.drawable.desert)
        val f = BitmapFactory.decodeResource(getResources(), R.drawable.forest)
        val g = BitmapFactory.decodeResource(getResources(), R.drawable.gas)
        val i = BitmapFactory.decodeResource(getResources(), R.drawable.ice)
        val l = BitmapFactory.decodeResource(getResources(), R.drawable.land)
        val m = BitmapFactory.decodeResource(getResources(), R.drawable.mountain)
        val r = BitmapFactory.decodeResource(getResources(), R.drawable.rock)
        val w = BitmapFactory.decodeResource(getResources(), R.drawable.water)


        val merged = Bitmap.createBitmap(300, 200, d.config)
        val canvas = Canvas(merged)
        canvas.drawBitmap(i, 0f, 0f, null)
        canvas.drawBitmap(i, 50f, 0f, null)
        canvas.drawBitmap(i, 150f, 0f, null)
        canvas.drawBitmap(m, 200f, 0f, null)
        canvas.drawBitmap(m, 250f, 0f, null)
        canvas.drawBitmap(i, 300f, 0f, null)
        canvas.drawBitmap(l, 0f, 50f, null)
        canvas.drawBitmap(l, 50f, 50f, null)
        canvas.drawBitmap(l, 150f, 50f, null)
        canvas.drawBitmap(f, 200f, 50f, null)
        canvas.drawBitmap(f, 250f, 50f, null)
        canvas.drawBitmap(w, 300f, 50f, null)
        canvas.drawBitmap(w, 0f, 100f, null)
        canvas.drawBitmap(l, 50f, 100f, null)
        canvas.drawBitmap(l, 150f, 100f, null)
        canvas.drawBitmap(l, 200f, 100f, null)
        canvas.drawBitmap(i, 250f, 100f, null)
        canvas.drawBitmap(w, 300f, 100f, null)
        canvas.drawBitmap(w, 0f, 100f, null)
        canvas.drawBitmap(r, 50f, 100f, null)
        canvas.drawBitmap(r, 150f, 100f, null)
        canvas.drawBitmap(i, 200f, 100f, null)
        canvas.drawBitmap(i, 250f, 100f, null)
        canvas.drawBitmap(i, 300f, 100f, null)

        planet.setImageBitmap(merged)

    }

    /** Called when the user taps the Create button */
    fun sendCreate(view: View) {
//        val editText = findViewById<EditText>(R.id.editText)
//        val message = editText.text.toString()
//        val intent = Intent(this, CreateUniverseActivity::class.java).apply {
//            putExtra(EXTRA_MESSAGE, message)
//        }
//        startActivity(intent)

        output.setText("")

        Thread(Runnable {

            // Capture output from tester in an byte array
            val baos = ByteArrayOutputStream()
            val ps = PrintStream(baos)
            System.setOut(ps)

            tester.makeUniverse()

            System.out.flush()

            view.post { // This is going to the button's UI thread, but that's the same as the ScrollView
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

            tester.doUniverse()

            System.out.flush()

            view.post { // This is going to the button's UI thread, but that's the same as the ScrollView
                output.append(baos.toString())
            }

        }).start()


    }





}
