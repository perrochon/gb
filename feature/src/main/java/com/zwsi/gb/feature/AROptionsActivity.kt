package com.zwsi.gb.feature

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*
import com.zwsi.gb.feature.GBViewModel.Companion.showClickTargets
import com.zwsi.gb.feature.GBViewModel.Companion.showStats
import com.zwsi.gb.feature.GBViewModel.Companion.superSensors

class AROptionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

        // Version TextView
        val version = findViewById<TextView>(R.id.version)
        version.setText(BuildConfig.VERSIONNAME)

        // Done Button
        val doneButton: Button = findViewById(R.id.DoneButton)
        doneButton.setOnClickListener(View.OnClickListener {
            finish()
        })

        val sharedPref = this.getSharedPreferences("options", Context.MODE_PRIVATE)

        val superSensorsButton: Switch = findViewById(R.id.SuperSensors)
        superSensorsButton.isChecked = superSensors
        superSensorsButton.setOnCheckedChangeListener { _, isChecked ->
            with(sharedPref.edit()) {
                putBoolean("superSensors", isChecked)
                commit()
            }
            GBViewModel.updatePrefs()
        }

        val showStatsButton: Switch = findViewById(R.id.ShowStats)
        showStatsButton.isChecked = showStats
        showStatsButton.setOnCheckedChangeListener { _, isChecked ->
            with(sharedPref.edit()) {
                putBoolean("showStats", isChecked)
                commit()
            }
            GBViewModel.updatePrefs()
        }

        val showClickTargetsButton: Switch = findViewById(R.id.ShowClickTargets)
        showClickTargetsButton.isChecked = showClickTargets
        showClickTargetsButton.setOnCheckedChangeListener { _, isChecked ->
            with(sharedPref.edit()) {
                putBoolean("showClickTargets", isChecked)
                commit()
            }
            GBViewModel.updatePrefs()
        }



    }

}
