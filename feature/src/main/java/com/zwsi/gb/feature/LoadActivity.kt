package com.zwsi.gb.feature

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.TextView

class LoadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load)

        // Set up the Version View
        val version = findViewById<TextView>(R.id.version)
        version.setText(BuildConfig.VERSIONNAME) // for now: 0.0.0.~ #commits...

        var lastSelection = 0
        val buttons = arrayListOf<Button>()

        // TODO Reduce the code duplication below
        val new1Button: Button = findViewById(R.id.New1Button)
        buttons.add(new1Button)
        new1Button.setOnClickListener(View.OnClickListener {
            lastSelection = 10
            GlobalStuff.handleClick(it, buttons)
        })

        val new2Button: Button = findViewById(R.id.New2Button)
        buttons.add(new2Button)
        new2Button.setOnClickListener(View.OnClickListener {
            lastSelection = 20
            GlobalStuff.handleClick(it, buttons)
        })

        val load1Button1: Button = findViewById(R.id.Load1Button1)
        buttons.add(load1Button1)
        load1Button1.setOnClickListener(View.OnClickListener {
            lastSelection = 11
            GlobalStuff.handleClick(it, buttons)
        })

        val load1Button2: Button = findViewById(R.id.Load1Button2)
        buttons.add(load1Button2)
        load1Button2.setOnClickListener(View.OnClickListener {
            lastSelection = 12
            GlobalStuff.handleClick(it, buttons)
        })

        val load1Button3: Button = findViewById(R.id.Load1Button3)
        buttons.add(load1Button3)
        load1Button3.setOnClickListener(View.OnClickListener {
            lastSelection = 13
            GlobalStuff.handleClick(it, buttons)
        })

        val load1Button4: Button = findViewById(R.id.Load1Button4)
        buttons.add(load1Button4)
        load1Button4.setOnClickListener(View.OnClickListener {
            lastSelection = 14
            GlobalStuff.handleClick(it, buttons)
        })

        val load1Button5: Button = findViewById(R.id.Load1Button5)
        buttons.add(load1Button5)
        load1Button5.setOnClickListener(View.OnClickListener {
            lastSelection = 15
            GlobalStuff.handleClick(it, buttons)
        })

        val load1Button6: Button = findViewById(R.id.Load1Button6)
        buttons.add(load1Button6)
        load1Button6.setOnClickListener(View.OnClickListener {
            lastSelection = 16
            GlobalStuff.handleClick(it, buttons)
        })

        val load2Button1: Button = findViewById(R.id.Load2Button1)
        buttons.add(load2Button1)
        load2Button1.setOnClickListener(View.OnClickListener {
            lastSelection = 21
            GlobalStuff.handleClick(it, buttons)
        })

        val load2Button2: Button = findViewById(R.id.Load2Button2)
        buttons.add(load2Button2)
        load2Button2.setOnClickListener(View.OnClickListener {
            lastSelection = 22
            GlobalStuff.handleClick(it, buttons)
        })

        val load2Button3: Button = findViewById(R.id.Load2Button3)
        buttons.add(load2Button3)
        load2Button3.setOnClickListener(View.OnClickListener {
            lastSelection = 23
            GlobalStuff.handleClick(it, buttons)
        })

        val doneButton: Button = findViewById(R.id.DoneButton)
        doneButton.setOnClickListener(View.OnClickListener {

            when (lastSelection) {
                10 -> GlobalStuff.makeUniverse(it, false)
                20 -> GlobalStuff.makeUniverse(it, true)
                in 11..16, in 21..23 -> GlobalStuff.loadUniverse(it, lastSelection)
            }

            finish()
        })

        val buttonCancel = findViewById<Button>(R.id.cancelDestination)

        buttonCancel.setOnClickListener(View.OnClickListener {
            finish()
        })



    }

}
