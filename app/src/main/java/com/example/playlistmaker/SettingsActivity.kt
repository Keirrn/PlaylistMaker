package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val shareButton = findViewById<ImageButton>(R.id.shareButton)
        shareButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.course_link))
            }
            startActivity(Intent.createChooser(intent, getString(R.string.share_dialog_title)))
        }

        val agreementButton = findViewById<ImageButton>(R.id.agreementButton)
        agreementButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(getString(R.string.offer_link))
            }
            startActivity(intent)
        }

        val supportButton = findViewById<ImageButton>(R.id.supportButton)
        supportButton.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_email_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.support_email_body))
            }
            startActivity(supportIntent)
        }
    }
}