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

        //val planet = findViewById<ImageView>(R.id.planet)
        //planet.setImageResource(R.drawable.ice)


        val planet = findViewById<ImageView>(R.id.planet)
        //val b = BitmapFactory.decodeResource(R.drawable.ice, R.drawable.ice)
        //image.setImageBitmap(b)

        val image1 = BitmapFactory.decodeResource(getResources(), R.drawable.forest)
        val image2 = BitmapFactory.decodeResource(getResources(), R.drawable.ice)
        //planet.setImageBitmap(image1)

        val merged = Bitmap.createBitmap(100, 50, image1.config)
        val canvas = Canvas(merged)
        canvas.drawBitmap(image1, 0f, 0f, null)
        canvas.drawBitmap(image2, 50f, 0f, null)

        planet.setImageBitmap(merged)


        //    Bitmap bitmap;
        ////Convert bitmap to drawable
        //    Drawable drawable = new BitmapDrawable(getResources(), bitmap);
        //    planet.setImageDrawable(drawable);

        // that's a bit more complex, use Bitmap.createBitmap(int width, int height, Bitmap.Config config);
        // to create a mutable bitmap with the size you want, create a new canvas with new Canvas(bitmap);
        // to create a canvas into that bitmap, and use canvas.drawBitmap(...) to draw the 4 bitmaps into the final one.

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
