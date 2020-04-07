package com.zwsi.ar.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.zwsi.ar.app.ARViewModel.Companion.showClickTargets
import com.zwsi.ar.app.ARViewModel.Companion.showContButton
import com.zwsi.ar.app.ARViewModel.Companion.showRaceStats
import com.zwsi.ar.app.ARViewModel.Companion.showStats
import com.zwsi.ar.app.ARViewModel.Companion.superSensors
import com.zwsi.ar.app.ARViewModel.Companion.vm
import com.zwsi.gblib.GBController
import kotlinx.android.synthetic.main.activity_options.*

class AROptionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

        text_version.text=BuildConfig.VERSIONNAME

        HelpButtonOptions.setOnClickListener {
            if (!GlobalStuff.doubleClick()) {
                val intent = Intent(this, ARHelpActivity::class.java)
                val b = Bundle()
                b.putString("url", "file:///android_asset/optionsHelp.html")
                intent.putExtras(b)
                startActivity(intent)
            }
        }

        button_done.setOnClickListener {
            finish()
        }

        val sharedPref = this.getSharedPreferences("options", Context.MODE_PRIVATE)

        switch_hyper_sensors.isChecked = superSensors
        switch_hyper_sensors.setOnCheckedChangeListener { _, isChecked ->
            with(sharedPref.edit()) {
                putBoolean("superSensors", isChecked)
                apply()
            }
            ARViewModel.updatePrefs()
        }

        switch_perf_stats.isChecked = showStats
        switch_perf_stats.setOnCheckedChangeListener { _, isChecked ->
            with(sharedPref.edit()) {
                putBoolean("showStats", isChecked)
                apply()
            }
            ARViewModel.updatePrefs()
        }

        switch_race_stats.isChecked = showRaceStats
        switch_race_stats.setOnCheckedChangeListener { _, isChecked ->
            with(sharedPref.edit()) {
                putBoolean("showRaceStats", isChecked)
                apply()
            }
            ARViewModel.updatePrefs()
        }

        switch_click_targets.isChecked = showClickTargets
        switch_click_targets.setOnCheckedChangeListener { _, isChecked ->
            with(sharedPref.edit()) {
                putBoolean("showClickTargets", isChecked)
                apply()
            }
            ARViewModel.updatePrefs()
        }

        switch_continuous.isChecked = showContButton
        switch_continuous.setOnCheckedChangeListener { _, isChecked ->
            with(sharedPref.edit()) {
                putBoolean("showContButton", isChecked)
                apply()
            }
            ARViewModel.updatePrefs()
            ARViewModel.actionsTaken.value = System.currentTimeMillis().toInt()

        }

        switch_demo_mode.isChecked
        switch_demo_mode.setOnCheckedChangeListener { _, isChecked ->
            vm.demoMode = isChecked
            GBController.setDemoMode(isChecked)
            ARViewModel.actionsTaken.value = System.currentTimeMillis().toInt()
        }
    }

}
