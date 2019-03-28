package com.zwsi.gb.feature

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.TextView

class ARPlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        // Version TextView
        val version = findViewById<TextView>(R.id.version)
        version.setText(BuildConfig.VERSIONNAME)

        // Done Button
        val doneButton: Button = findViewById(R.id.DoneButton)
        doneButton.setOnClickListener(View.OnClickListener {
            finish()
        })

        val statsTextView: TextView = findViewById(R.id.missionResults)
        statsTextView.append("Mission 1: Achieved in 24 turns.\n")
        statsTextView.append("Mission 2: Achieved in 123 turns.\n")
        statsTextView.append("Mission 2: Achieved in 223 turns.\n")

    }
}
