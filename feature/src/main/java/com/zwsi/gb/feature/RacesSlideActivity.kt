package com.zwsi.gb.feature

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.widget.TextView
import android.widget.Toast
import com.zwsi.gblib.GBController

class RacesSlideActivity : AppCompatActivity() {

    private lateinit var viewpager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_races_slide)

        val title = if (intent.hasExtra("title")) intent.getStringExtra("title") else "Some Races"

        val titleTextView = findViewById<TextView>(R.id.text_races_title)
        titleTextView.setText(title)


        initViews()
        setupViewPager()
    }

    private fun initViews() {
        viewpager = findViewById(R.id.viewpager)
    }

    private fun setupViewPager() {

        val adapter = MyFragmentPagerAdapter(getSupportFragmentManager())

        var displayRaces: ArrayList<Int>
        if (intent.hasExtra("races")) {
            displayRaces = intent.getIntegerArrayListExtra("races")
        } else {
            displayRaces = ArrayList<Int>()
            for (r in GBController.universe.allRaces) {
                displayRaces.add(r.uid)
            }
        }


        for (raceUID in displayRaces) {
            var rf: RaceFragment = RaceFragment.newInstance(raceUID.toString())
            adapter.addFragment(rf, raceUID.toString())
        }


        viewpager.adapter = adapter

    }
}