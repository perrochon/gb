package com.zwsi.gb.feature

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.zwsi.gblib.GBController
import com.zwsi.gblib.GBPlanet

class PlanetsSlideActivity : AppCompatActivity() {

    private lateinit var viewpager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planets_slide)

        val title = if (intent.hasExtra("title")) intent.getStringExtra("title") else "Planets"
        val titleTextView = findViewById<TextView>(R.id.text_planets_title)
        titleTextView.setText(title)

        initViews()
        setupViewPager()

        val planetUID = getIntent().getIntExtra("shipUID", -1)
        if (planetUID > 0) {
            viewpager.setCurrentItem(planetUID)
        }

    }

    private fun initViews() {
        viewpager = findViewById(R.id.viewpager)
    }

    private fun setupViewPager() {

        val adapter = MyFragmentPagerAdapter(getSupportFragmentManager())

        // Figure out which items to display
        val displayList: ArrayList<Int>
        if (intent.hasExtra("planets")) {
            displayList = intent.getIntegerArrayListExtra("planets")
        } else {
            displayList = ArrayList<Int>()
            for (i in GBController.universe.allPlanets) {
                displayList.add(i.uid)
            }
        }

        // Adding a fregment for each item we want to display
        for (uid in displayList) {
            val fragment: PlanetFragment = PlanetFragment.newInstance(uid.toString())
            adapter.addFragment(fragment, uid.toString())
        }

        viewpager.adapter = adapter

    }


    /** Called when the user taps the Make Pod button */
    fun makeFactory(view: View) {

        val parent = view.parent as View
        val planet: GBPlanet = parent.tag as GBPlanet

        val universe = GBController.universe!!
        universe.makeFactory(planet)

        val message = "Ordered Factory on " + planet.name
        Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()

    }

    /** Called when the user taps the Colonize button */
    fun colonize0(view: View) {

        val parent = view.parent as View
        val planet: GBPlanet = parent.tag as GBPlanet

        val universe = GBController.universe!!
        universe.landPopulation(planet, 0, 100)

        val message = "God Level: Landing Xenos on " + planet.name
        Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()
    }

    /** Called when the user taps the Colonize button */
    fun colonize1(view: View) {

        val parent = view.parent as View
        val planet: GBPlanet = parent.tag as GBPlanet

        val universe = GBController.universe!!
        universe.landPopulation(planet, 1, 100)

        val message = "God Level: Landing Impi on " + planet.name
        Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()
    }
}
