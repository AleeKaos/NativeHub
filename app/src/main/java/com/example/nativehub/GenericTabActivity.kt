package com.example.nativehub

import android.os.Build
import android.os.Bundle
import android.webkit.WebSettings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

/**
 * Activity genérica para todas as abas.
 * Cada aba é lançada em um processo separado (:tab2, :tab3, etc)
 * através do AndroidManifest.xml
 */
class GenericTabActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Extrair índice da aba do intent
        val tabIndex = intent.getIntExtra("tab_index", 1)
        val siteName = intent.getStringExtra("site_name") ?: ""
        val siteUrl = intent.getStringExtra("site_url") ?: ""

        Log.d("GenericTabActivity", "Tab $tabIndex - Site: $siteName - URL: $siteUrl")

        setContent {
            TabScreen(
                tabIndex = tabIndex,
                siteName = siteName,
                siteUrl = siteUrl
            )
        }
    }
}
