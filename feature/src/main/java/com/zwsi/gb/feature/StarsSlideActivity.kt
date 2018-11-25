package com.zwsi.gb.feature

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.zwsi.gblib.GBController
import com.zwsi.gblib.GBShip
import com.zwsi.gblib.GBStar

class StarsSlideActivity : AppCompatActivity() {

    private lateinit var viewpager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stars_slide)

        val title = if (intent.hasExtra("title")) intent.getStringExtra("title") else "Stars"
        val titleTextView = findViewById<TextView>(R.id.text_stars_title)
        titleTextView.setText(title)

        initViews()
        setupViewPager()

        val starUID = getIntent().getIntExtra("starUID", -1)
        if (starUID > 0) {
            viewpager.setCurrentItem(starUID)
        }

    }

    private fun initViews() {
        viewpager = findViewById(R.id.viewpager)
    }

    private fun setupViewPager() {

        val adapter = MyFragmentPagerAdapter(getSupportFragmentManager())

        // Figure out which items to display
        val displayList: ArrayList<Int>
        if (intent.hasExtra("stars")) {
            displayList = intent.getIntegerArrayListExtra("stars")
        } else {
            displayList = ArrayList<Int>()
            for (i in GBController.universe.allStars) {
                displayList.add(i.uid)
            }
        }

        // Adding a fregment for each item we want to display
        for (uid in displayList) {
            val fragment: StarFragment = StarFragment.newInstance(uid.toString())
            adapter.addFragment(fragment, uid.toString())
        }

        viewpager.adapter = adapter

    }

    /** Called when the user taps the Go button */
    fun goToLocation(view: View) {

        val intent = Intent(this, PlanetsSlideActivity::class.java)

        val parent = view.parent as View
        val star = parent.tag as GBStar

        Toast.makeText(view.context, "Going to planets of " + star.name, Toast.LENGTH_SHORT).show()

        val displayUID = ArrayList<Int>()
        for (planet in star.starPlanets) {
            displayUID.add(planet.uid)
            intent.putExtra("planets", displayUID)
            intent.putExtra("title", "Planets of " + star.name)
        }
        startActivity(intent)

    }


}