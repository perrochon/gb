package com.zwsi.ar.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.zwsi.gblib.GBData.Companion.Mission1
import com.zwsi.gblib.GBData.Companion.Mission2
import com.zwsi.gblib.GBData.Companion.Mission3
import com.zwsi.gblib.GBData.Companion.Mission4
import com.zwsi.gblib.GBData.Companion.Mission5
import com.zwsi.gblib.GBData.Companion.Mission6
import kotlinx.android.synthetic.main.activity_player.*

class ARPlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        text_version.text = BuildConfig.VERSIONNAME

        button_done.setOnClickListener {
            finish()
        }

        missionResults.text = ""
        val sharedPref = this.getSharedPreferences("playerstats", MODE_PRIVATE)

        for (i in arrayOf(Mission1, Mission2, Mission3, Mission4, Mission5, Mission6)) {
            val days = sharedPref!!.getInt(i, -1)
            if (days > 0) {
                missionResults.append("${i}: Achieved in $days turns.\n\n")
            } else {
                missionResults.append("${i}: Not achieved yet.\n\n")
            }
        }
    }
}
