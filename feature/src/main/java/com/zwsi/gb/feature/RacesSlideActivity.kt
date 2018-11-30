package com.zwsi.gb.feature

import android.os.Bundle
import android.os.SystemClock
import android.support.v4.R.id.left
import android.support.v4.R.id.right
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.zwsi.gblib.GBController
import com.zwsi.gblib.GBRace

class RacesSlideActivity : AppCompatActivity() {

    private lateinit var viewpager: ViewPager
    private var startItem = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_races_slide)

        val title = if (intent.hasExtra("title")) intent.getStringExtra("title") else "Some Races"
        val titleTextView = findViewById<TextView>(R.id.text_races_title)
        titleTextView.setText(title)

        initViews()
        setupViewPager()

        viewpager.setCurrentItem(startItem)

    }

    private fun initViews() {
        viewpager = findViewById(R.id.viewpager)
    }

    private fun setupViewPager() {

        val adapter = MyFragmentPagerAdapter(getSupportFragmentManager())
        val startUID = getIntent().getIntExtra("UID", -1)

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
            if (uid == startUID)
                startItem = adapter.count-1
        }

        viewpager.adapter = adapter

        viewpager.setClipToPadding(false)
        viewpager.setPadding(50,0,50,0)

    }

    /** Called when the user taps the Go button */
    fun goToLocation(view: View) {

        if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
            return;
        }
        lastClickTime = SystemClock.elapsedRealtime();


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