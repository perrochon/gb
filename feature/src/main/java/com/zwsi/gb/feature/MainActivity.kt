package com.zwsi.gb.feature

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream
import java.io.PrintStream


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        System.setOut(PrintStream(object:OutputStream() {
            internal var outputStream = ByteArrayOutputStream()
            @Throws(IOException::class)
            override fun write(oneByte:Int) {
                outputStream.write(oneByte)
                output.setText(String(outputStream.toByteArray()))
            }
        }))
        // Doing this the simple way with scrolling TextView.
        // May need to upgrade to ScrollView to get kinetic scroll
        output.setMovementMethod(android.text.method.ScrollingMovementMethod())
        com.zwsi.gblib.GBTest.main(arrayOf(""))


    }
}
