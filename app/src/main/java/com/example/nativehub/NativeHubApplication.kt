package com.example.nativehub

import android.app.Application
import android.os.Build
import android.util.Log
import android.webkit.WebView

class NativeHubApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Application.getProcessName() só existe a partir da API 28 (P).
        // Como minSdk = 24, chamá-lo incondicionalmente derrubava o app
        // (NoSuchMethodError) em API 24-27. Por isso toda a leitura do nome
        // do processo agora fica dentro do guard de versão.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

            val processName = getProcessName()

            Log.d(
                "NATIVEHUB",
                "PROCESSO = $processName"
            )

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
        } else {

            Log.d(
                "NATIVEHUB",
                "SDK < P - setDataDirectorySuffix não aplicável nesta versão"
            )
        }
    }
}