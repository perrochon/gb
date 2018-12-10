package com.zwsi.gb.feature

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.graphics.PointF
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP

import com.zwsi.gblib.GBController.Companion.universe

class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val imageView = findViewById<MapView>(R.id.imageViewScale)!!

        val turnObserver = Observer<Int> { newTurn ->
            imageView.turn = newTurn; imageView.invalidate()
        }  // TODO why is newTurn nullable?
        GBViewModel.curentTurn.observe(this, turnObserver)

    }

    /** Called when the user taps the Do button */
    fun doUniverse(view: View) {
        GlobalButtonOnClick.doUniverse(view)
    }

    fun continuousDo(view: View) {
        GlobalButtonOnClick.toggleContinuous(view)
    }

}
