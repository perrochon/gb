package com.zwsi.gb.feature

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.view.View
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.*

// To redirect stdout to the text view
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream
import java.io.PrintStream


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Doing this the simple way with scrolling TextView.
        // May need to upgrade to ScrollView to get kinetic scroll
        output.setMovementMethod(android.text.method.ScrollingMovementMethod())

        System.setOut(PrintStream(object:OutputStream() {
            internal var outputStream = ByteArrayOutputStream()
            @Throws(IOException::class)
            override fun write(oneByte:Int) {
                outputStream.write(oneByte)
                output.setText(String(outputStream.toByteArray()))
            }
        }))


    }

    /** Called when the user taps the Create button */
    fun sendCreate(view: View) {
        val editText = findViewById<EditText>(R.id.editText)
        val message = editText.text.toString()
        val intent = Intent(this, CreateUniverseActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, message)
        }
        startActivity(intent)
//        com.zwsi.gblib.GBTest.main(arrayOf(""))
    }

    /** Called when the user taps the Do button */
    fun sendDo(view: View) {
    }
}
