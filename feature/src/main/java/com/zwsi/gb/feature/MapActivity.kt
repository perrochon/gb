package com.zwsi.gb.feature

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

        val imageView = findViewById<SubsamplingScaleImageView>(R.id.imageViewScale)!!

        val fullResImage = ImageSource.resource(R.drawable.orion18000)
        val lowResImage = ImageSource.resource(R.drawable.orion1024)

        fullResImage.dimensions(18000,18000)

        imageView.setImage(fullResImage, lowResImage);

        val home = universe.allRaces[0].home.star

        imageView.setMinimumScaleType(SCALE_TYPE_CENTER_CROP)
        imageView.setDoubleTapZoomScale(1.5f)

        imageView.setScaleAndCenter(1.5f, PointF(home.loc.x*18f, home.loc.y*18f)) //TODO replace 18f with uToS
        // TODO reset this after recreating the universe

    }

    /** Called when the user taps the Do button */
    fun doUniverse(view: View) {
        GlobalButtonOnClick.doUniverse(view)
    }

    fun continuousDo(view: View) {
        GlobalButtonOnClick.toggleContinuous(view)
    }

}
