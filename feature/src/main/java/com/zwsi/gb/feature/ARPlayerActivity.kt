package com.zwsi.gb.feature

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.zwsi.gb.feature.GBViewModel.Companion.vm
import com.zwsi.gb.feature.GBViewModel.Companion.missionResults
import com.zwsi.gb.feature.GBViewModel.Companion.missionResultsString

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
        for (i in 0 until missionResults.size) {
            if (missionResults[i]>0) {
                statsTextView.append("Mission ${i + 1}: Achieved in ${missionResults[i]} turns.\n")
            } else {
                statsTextView.append("Mission ${i + 1}: Not achieved yet.\n")
            }
        }
    }
}
