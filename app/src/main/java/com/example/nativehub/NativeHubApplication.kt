package com.example.nativehub

import android.app.Application
import android.os.Build
import android.util.Log
import android.webkit.WebView

class NativeHubApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val processName = getProcessName()

        Log.d(
            "NATIVEHUB",
            "PROCESSO = $processName"
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

            if (processName != packageName) {

                try {

                    WebView.setDataDirectorySuffix(
                        processName.replace(":", "_")
                    )

                    Log.d(
                        "NATIVEHUB",
                        "SUFIXO = $processName"
                    )

                } catch (e: Exception) {

                    Log.e(
                        "NATIVEHUB",
                        "ERRO SUFIXO",
                        e
                    )
                }
            }
        }
    }
}