package com.zwsi.ar.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView

class ARHelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        // Version TextView
        val version = findViewById<TextView>(R.id.version)
        version.text = BuildConfig.VERSIONNAME

        // Done Button
        val doneButton: Button = findViewById(R.id.DoneButton)
        doneButton.setOnClickListener(View.OnClickListener {
            finish()
        })

//        val helpTextView: TextView = findViewById(R.id.helptext)
//        helpTextView.text = HtmlCompat.fromHtml(getString(R.string.mainhelp), HtmlCompat.FROM_HTML_MODE_LEGACY)
//        helpTextView.movementMethod = LinkMovementMethod.getInstance()

        val b = intent.extras
        val helpUrl = b?.getString("url") ?: "file:///android_asset/mainHelp.html"

        val helpWebView: WebView = findViewById(R.id.helpweb)
        helpWebView.setBackgroundColor(0x00000000)
        helpWebView.loadUrl(helpUrl);
        // TODO FIXME Use Roboto font, ideally without packaging the whole font into the assets directory...
        // TODO FIXME Where to put display instructions? In each HTML file??? CSS file?
        // TODO FIXME Now that it's proper HTML again, we can link to it on Github
        // TODO FIXME Use custom fonts on Web (so maybe do want to add them to directory and pack them)
    }
}
