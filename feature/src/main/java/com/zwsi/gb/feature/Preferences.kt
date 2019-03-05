package com.zwsi.gb.feature

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SwitchCompat
import android.widget.Switch
import android.widget.Toast
import android.widget.ToggleButton
import com.zwsi.gb.feature.GBViewModel.Companion.showClickTargets
import com.zwsi.gb.feature.GBViewModel.Companion.showStats
import com.zwsi.gb.feature.GBViewModel.Companion.superSensors

class Preferences : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)

        val sharedPref = this.getSharedPreferences("options", Context.MODE_PRIVATE)

        val superSensorsButton: Switch = findViewById(R.id.SuperSensors)
        superSensorsButton.isChecked = superSensors
        superSensorsButton.setOnCheckedChangeListener { it, isChecked ->
            with(sharedPref.edit()) {
                putBoolean("superSensors", isChecked)
                commit()
            }
            GBViewModel.updatePrefs()
            Toast.makeText(it.context, "Super Sensors is $isChecked", Toast.LENGTH_SHORT).show()

        }

        val showStatsButton: Switch = findViewById(R.id.ShowStats)
        showStatsButton.isChecked = showStats
        showStatsButton.setOnCheckedChangeListener { it, isChecked ->
            with(sharedPref.edit()) {
                putBoolean("showStats", isChecked)
                commit()
            }
            GBViewModel.updatePrefs()
            Toast.makeText(it.context, "Show Stats is $isChecked", Toast.LENGTH_SHORT).show()

        }

        val showClickTargetsButton: Switch = findViewById(R.id.ShowClickTargets)
        showClickTargetsButton.isChecked = showClickTargets
        showClickTargetsButton.setOnCheckedChangeListener { it, isChecked ->
            with(sharedPref.edit()) {
                putBoolean("showClickTargets", isChecked)
                commit()
            }
            GBViewModel.updatePrefs()
            Toast.makeText(it.context, "Show Click Targets is $isChecked", Toast.LENGTH_SHORT).show()

        }

    }

}
