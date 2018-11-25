package com.zwsi.gb.feature

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.zwsi.gblib.GBController
import com.zwsi.gblib.GBRace
import com.zwsi.gblib.GBShip

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

        val raceUID = getIntent().getIntExtra("shipUID", -1)
        if (raceUID > 0) {
            viewpager.setCurrentItem(raceUID)
        }

    }

    private fun initViews() {
        viewpager = findViewById(R.id.viewpager)
    }

    private fun setupViewPager() {

        val adapter = MyFragmentPagerAdapter(getSupportFragmentManager())

        // Figure out which items to display
        val displayList: ArrayList<Int>
        if (intent.hasExtra("races")) {
            displayList = intent.getIntegerArrayListExtra("races")
        } else {
            displayList = ArrayList<Int>()
            for (i in GBController.universe.allRaces) {
                displayList.add(i.uid)
            }
        }

        // Adding a fregment for each item we want to display
        for (uid in displayList) {
            val fragment: RaceFragment = RaceFragment.newInstance(uid.toString())
            adapter.addFragment(fragment, uid.toString())
        }


        viewpager.adapter = adapter

    }

    /** Called when the user taps the Go button */
    fun goToLocation(view: View) {

        val parent = view.parent as View
        val race = parent.tag as GBRace

        Toast.makeText(view.context, race.name, Toast.LENGTH_SHORT).show()

//        val displayUID = ArrayList<Int>()
//        for (ship in race.)
//
//        displayUID.add(ship.locationuid)
//            intent.putExtra("planets", displayUID)
//


    }
}