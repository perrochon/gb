package com.zwsi.gb.feature

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
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

        val displayList: ArrayList<Int>
        if (intent.hasExtra("races")) {
            displayList = intent.getIntegerArrayListExtra("races")
        } else {
            displayList = ArrayList<Int>()
            for (i in GBController.universe.allRaces) {
                displayList.add(i.uid)
            }
        }


        for (uid in displayList) {
            val fragment: RaceFragment = RaceFragment.newInstance(uid.toString())
            adapter.addFragment(fragment, uid.toString())
        }


        viewpager.adapter = adapter

    }
}