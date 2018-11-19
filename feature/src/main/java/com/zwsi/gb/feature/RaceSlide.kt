package com.zwsi.gb.feature

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager

class RaceSlide : AppCompatActivity() {

    private lateinit var viewpager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_race_slide)

        initViews()
        setupViewPager()
    }

    private fun initViews() {
        viewpager = findViewById(R.id.viewpager)
    }

    private fun setupViewPager() {

        val adapter = MyFragmentPagerAdapter(getSupportFragmentManager())

        var race = 0
        for (i in 1..100) {
            var r0: RaceFragment = RaceFragment.newInstance("0")
            adapter.addFragment(r0, race++.toString())
            var r1: RaceFragment = RaceFragment.newInstance("1")
            adapter.addFragment(r1, race++.toString())
        }

        viewpager.adapter = adapter

    }
}
