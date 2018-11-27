package com.zwsi.gb.feature

import android.graphics.PointF
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP

import com.zwsi.gblib.GBController

class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val imageView = findViewById<SubsamplingScaleImageView>(R.id.imageViewScale)!!

        val fullResImage = ImageSource.resource(R.drawable.orion18000)!!
        val lowResImage = ImageSource.resource(R.drawable.orion1024)!!

        fullResImage.dimensions(18000,18000)

        imageView.setImage(fullResImage, lowResImage);

        val home = GBController.universe.allStars[0]

        imageView.setMinimumScaleType(SCALE_TYPE_CENTER_CROP)
        imageView.setScaleAndCenter(1.9f, PointF(home.x*18f, home.y*18f))

    }


}
