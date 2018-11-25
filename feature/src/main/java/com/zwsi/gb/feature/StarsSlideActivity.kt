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

        val intent = getIntent()
        val starUID = intent.getIntExtra("starUID", -1)
        if (starUID > 0) {
            viewpager.setCurrentItem(starUID)
        }

    }

    private fun initViews() {
        viewpager = findViewById(R.id.viewpager)
    }

    private fun setupViewPager() {

        val adapter = MyFragmentPagerAdapter(getSupportFragmentManager())

        val displayList: ArrayList<Int>
        if (intent.hasExtra("stars")) {
            displayList = intent.getIntegerArrayListExtra("stars")
        } else {
            displayList = ArrayList<Int>()
            for (i in GBController.universe.allStars) {
                displayList.add(i.uid)
            }
        }

        for (uid in displayList) {
            val fragment: StarFragment = StarFragment.newInstance(uid.toString())
            adapter.addFragment(fragment, uid.toString())
        }

        viewpager.adapter = adapter

    }


}