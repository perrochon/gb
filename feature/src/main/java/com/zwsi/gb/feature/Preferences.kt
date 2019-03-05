package com.zwsi.gb.feature

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import android.widget.ToggleButton

class Preferences : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)

        val sharedPref = this.getSharedPreferences("options", Context.MODE_PRIVATE)

        val superSensorsButton: ToggleButton = findViewById(R.id.SuperSensors)
        superSensorsButton.isChecked = sharedPref.getBoolean("superSensors", true)
        superSensorsButton.setOnCheckedChangeListener { it, isChecked ->
            with(sharedPref.edit()) {
                putBoolean("superSensors", isChecked)
                commit()
            }

            Toast.makeText(it.context, "Super Sensors is $isChecked", Toast.LENGTH_SHORT).show()

        }



        val showStatsButton: ToggleButton = findViewById(R.id.ShowStats)
        showStatsButton.isChecked = sharedPref.getBoolean("showStats", false)
        showStatsButton.setOnCheckedChangeListener { it, isChecked ->
            with(sharedPref.edit()) {
                putBoolean("showStats", isChecked)
                commit()
            }

            Toast.makeText(it.context, "Show Stats is $isChecked", Toast.LENGTH_SHORT).show()

        }

        val showClickTargetsButton: ToggleButton = findViewById(R.id.ShowClickTargets)
        showClickTargetsButton.isChecked = sharedPref.getBoolean("showClickTargets", false)
        showClickTargetsButton.setOnCheckedChangeListener { it, isChecked ->
            with(sharedPref.edit()) {
                putBoolean("showClickTargets", isChecked)
                commit()
            }

            Toast.makeText(it.context, "Show Click Targets is $isChecked", Toast.LENGTH_SHORT).show()

        }

    }

}
