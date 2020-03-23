package com.zwsi.gb.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.zwsi.gblib.GBData.Companion.Mission1
import com.zwsi.gblib.GBData.Companion.Mission2
import com.zwsi.gblib.GBData.Companion.Mission3
import com.zwsi.gblib.GBData.Companion.Mission4
import com.zwsi.gblib.GBData.Companion.Mission5
import com.zwsi.gblib.GBData.Companion.Mission6

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

        val sharedPref = this.getSharedPreferences("playerstats", MODE_PRIVATE)

        for (i in arrayOf(Mission1, Mission2, Mission3, Mission4, Mission5, Mission6)) {

            val days = sharedPref!!.getInt(i, -1)
            if (days > 0) {
                statsTextView.append("${i}: Achieved in ${days} turns.\n\n")
            } else {
                statsTextView.append("${i}: Not achieved yet.\n\n")
            }

        }
    }
}
