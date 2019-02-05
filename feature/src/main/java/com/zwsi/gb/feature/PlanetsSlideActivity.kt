package com.zwsi.gb.feature

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView

class PlanetsSlideActivity : AppCompatActivity() {

    private lateinit var viewpager: ViewPager
    private var startItem = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planets_slide)

        val title = if (intent.hasExtra("title")) intent.getStringExtra("title") else "Planets"
        val titleTextView = findViewById<TextView>(R.id.text_planets_title)
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
        if (intent.hasExtra("planets")) {
            displayList = intent.getIntegerArrayListExtra("planets")
        } else {
            displayList = ArrayList<Int>()
            for ((_, planet) in GBViewModel.viewPlanets) {
                displayList.add(planet.uid)
            }
        }

        // Adding a fragment for each item we want to display
        for (uid in displayList) {
            val fragment: PlanetFragment = PlanetFragment.newInstance(uid.toString())
            adapter.addFragment(fragment, uid.toString())
            if (uid == startUID)
                startItem = adapter.count-1
        }

        viewpager.adapter = adapter

        viewpager.setClipToPadding(false)
        viewpager.setPadding(50,0,50,0)
        viewpager.setPageMargin(25) // TODO Otherwise planet views touch, as they go full screen. Can remove this if they no longer to

    }

    fun makeFactory(view:View) {
        GlobalButtonOnClick.makeFactory(view)
    }

}
