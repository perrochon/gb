package com.zwsi.ar.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.webkit.WebView
import android.widget.Button
import kotlinx.android.synthetic.main.activity_help.*

class ARHelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        text_version.text=BuildConfig.VERSIONNAME

        button_done.setOnClickListener {
            finish()
        }

        val b = intent.extras
        val helpUrl = b?.getString("url") ?: "file:///android_asset/mainHelp.html"

        webview_help.setBackgroundColor(0x00000000)
        webview_help.loadUrl(helpUrl)
        // TODO FIXME Use Roboto font, ideally without packaging the whole font into the assets directory...
        // TODO FIXME Where to put display instructions? In each HTML file??? CSS file?
        // TODO FIXME Now that it's proper HTML again, we can link to it on Github
        // TODO FIXME Use custom fonts on Web (so maybe do want to add them to directory and pack them)
    }
}
