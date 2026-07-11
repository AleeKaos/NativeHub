package com.example.nativehub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class EightUActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val siteName =
            intent.getStringExtra("site_name") ?: ""

        val siteUrl =
            intent.getStringExtra("site_url") ?: ""

        setContent {
            TabScreen(
                tabIndex = 1,
                siteName = siteName,
                siteUrl = siteUrl
            )
        }
    }
}