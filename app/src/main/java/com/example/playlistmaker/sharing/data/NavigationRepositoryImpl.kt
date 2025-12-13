package com.example.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.NavigationRepository

class NavigationRepositoryImpl (
    private val context: Context
) : NavigationRepository {

    override fun shareApp() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.course_link))
        }
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_dialog_title)))
    }

    override fun openAgreement() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(context.getString(R.string.offer_link))
        }
        context.startActivity(intent)
    }

    override fun contactSupport() {
        val supportIntent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.support_email)))
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.support_email_subject))
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.support_email_body))
        }
        context.startActivity(supportIntent)
    }
}