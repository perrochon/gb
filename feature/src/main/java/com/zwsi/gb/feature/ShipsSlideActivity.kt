package com.zwsi.gb.feature

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.zwsi.gblib.GBController

class ShipsSlideActivity : AppCompatActivity() {

    private lateinit var viewpager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ships_slide)

        initViews()
        setupViewPager()
    }

    private fun initViews() {
        viewpager = findViewById(R.id.viewpager)
    }

    private fun setupViewPager() {

        val adapter = MyFragmentPagerAdapter(getSupportFragmentManager())

        var ship = 0
        for (i in 1..100) {
            var sh1: ShipFragment = ShipFragment.newInstance("0")
            adapter.addFragment(sh1, ship++.toString())
            var sh2: ShipFragment = ShipFragment.newInstance("1")
            adapter.addFragment(sh2, ship++.toString())
            var sh3: ShipFragment = ShipFragment.newInstance("2")
            adapter.addFragment(sh3, ship++.toString())
        }

        viewpager.adapter = adapter

    }

    /** Called when the user taps the make Pod button */
    fun makePod(view: View) {

        val universe = GBController.universe

        //val message = "Landing Xenos on " + universe!!.allPlanets[view.id.toInt()].name
        Toast.makeText(view.context, "poof", Toast.LENGTH_LONG).show()

    }

    /** Called when the user taps the make Cruiser button */
    fun makeCruiser(view: View) {
        val universe = GBController.universe

        //val message = "Landing Impi on " + universe!!.allPlanets[view.id.toInt()].name
        Toast.makeText(view.context, "poof", Toast.LENGTH_LONG).show()

    }

}