package com.zwsi.gb.feature

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.zwsi.gblib.GBController
import com.zwsi.gblib.GBShip

class ShipsSlideActivity : AppCompatActivity() {

    private lateinit var viewpager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ships_slide)

        initViews()
        setupViewPager()

        if (GBController.universe.allShips.size == 0) {
            val hintText = this.findViewById<TextView>(R.id.hintTextView)
            hintText.visibility = (TextView.VISIBLE)
            hintText.setText(
                "You haven't built any ships yet.\n\n " +
                        "Start making ships by creating a Factory on your home planet. " +
                        "That Factory will be your first ship. Then order the Factory to make other ships. " +
                        "Remember that you have to give the order to build the factory first, then click on " +
                        "[Do] so your minions can execute your orders."
            )
        }

        val intent = getIntent()
        val shipUID = intent.getIntExtra("shipUID", -1)
        if (shipUID > 0) {
            viewpager.setCurrentItem(shipUID)
        }

    }

    private fun initViews() {
        viewpager = findViewById(R.id.viewpager)
    }

    private fun setupViewPager() {

        val adapter = MyFragmentPagerAdapter(getSupportFragmentManager())

        val universe = GBController.universe

        for (sh in universe.allShips) {

            var fragment: ShipFragment = ShipFragment.newInstance(sh.uid.toString())
            adapter.addFragment(fragment, sh.uid.toString())
        }

        viewpager.adapter = adapter

    }

    /** Called when the user taps the Go button */
    fun goToLocation(view: View) {
        val universe = GBController.universe

        val ship = view.tag as GBShip

        //val message = "Landing Impi on " + universe!!.allPlanets[view.id.toInt()].name
        Toast.makeText(view.context, ship.getLocation(), Toast.LENGTH_SHORT).show()

        if ((ship.level == 1) or (ship.level == 2)) {
            val intent = Intent(this, PlanetsSlideActivity::class.java)
            intent.putExtra("planetUID", ship.locationuid)
            startActivity(intent)
        }
        if (ship.level == 3) {
            val intent = Intent(this, StarsSlideActivity::class.java)
            intent.putExtra("starUID", ship.locationuid)
            startActivity(intent)
        }

    }

    /** Called when the user taps the make Pod button */
    fun makePod(view: View) {

        val universe = GBController.universe
        universe.makePod(universe.allShips[view.id.toInt()])

        val message = "Ordered Pod in Factory " + universe.allShips[view.id.toInt()].name
        Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()
    }

    /** Called when the user taps the make Cruiser button */
    fun makeCruiser(view: View) {
        val universe = GBController.universe

        //val message = "Landing Impi on " + universe!!.allPlanets[view.id.toInt()].name
        Toast.makeText(view.context, "Cruiser being made", Toast.LENGTH_SHORT).show()

    }

}