package com.zwsi.gb.feature

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.zwsi.gblib.GBController

class StarsSlideActivity : AppCompatActivity() {

    private lateinit var viewpager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stars_slide)

        initViews()
        setupViewPager()
    }

    private fun initViews() {
        viewpager = findViewById(R.id.viewpager)
    }

    private fun setupViewPager() {

        val adapter = MyFragmentPagerAdapter(getSupportFragmentManager())

        val universe = GBController.universe

        for (i in 0 until universe!!.allStars.size) { // TODO fix clumsy loop, also in planets
            var st: StarFragment = StarFragment.newInstance(i.toString())
            adapter.addFragment(st, i.toString())
        }

        viewpager.adapter = adapter

    }
}