@file:Suppress("DEPRECATION")

package com.example.nativehub

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun TabScreen(
    tabIndex: Int,
    siteName: String = "",
    siteUrl: String = "https://8u.com"
) {

    val context = LocalContext.current


    var webView by remember {
        mutableStateOf<WebView?>(null)
    }


    var tabs by remember {
        mutableStateOf(
            emptyList<TabItem>()
        )
    }

    // Track back stack para evitar sair prematuramente
    var backPressedTime by remember {
        mutableLongStateOf(0L)
    }


    fun reloadTabs() {

        val site =
            PrefsHelper
                .getSites(context)
                .firstOrNull {
                    it.name == siteName
                }


        tabs =
            site?.tabs
                ?.ifEmpty {

                    listOf(
                        TabItem(
                            name = "Principal",
                            url = siteUrl
                        )
                    )

                }
                ?: listOf(

                    TabItem(
                        name = "Principal",
                        url = siteUrl
                    )
                )
    }



    LaunchedEffect(
        siteName,
        tabIndex
    ) {

        reloadTabs()
    }



    LifecycleEventEffect(
        Lifecycle.Event.ON_RESUME
    ) {

        reloadTabs()
    }



    val safeIndex =
        tabIndex.coerceAtLeast(1)



    val currentTab =
        tabs.getOrNull(
            safeIndex - 1
        )
            ?: tabs.firstOrNull()
            ?: TabItem(
                name = "Principal",
                url = siteUrl
            )



    BackHandler {

        if (webView?.canGoBack() == true) {
            webView?.goBack()
        } else {
            val currentTime = System.currentTimeMillis()
            
            // Se o usuário apertou back em menos de 2 segundos novamente, sair da aba
            if (currentTime - backPressedTime < 2000) {
                (context as? Activity)?.finish()
            } else {
                // Primeiro back: apenas atualiza o timestamp
                backPressedTime = currentTime
            }
        }
    }

    // Cleanup ao sair
    DisposableEffect(Unit) {
        onDispose {
            webView?.stopLoading()
            webView?.webChromeClient = null
            webView?.webViewClient = WebViewClient()
            webView?.destroy()
            webView = null
            Log.d("TabScreen", "WebView disposed")
        }
    }


    Column(
        modifier =
            Modifier.fillMaxSize()
    ) {

        // BARRA SUPERIOR: Home + Abas + Configurações
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // Botão Home (esquerda)
            IconButton(
                onClick = {
                    (context as? Activity)?.finish()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar para Home"
                )
            }

            // Abas scrolláveis (centro, ocupando o espaço)
            if (tabs.isNotEmpty()) {
                PrimaryScrollableTabRow(

                    selectedTabIndex =
                        (safeIndex - 1)
                            .coerceAtMost(
                                (tabs.size - 1)
                                    .coerceAtLeast(0)
                            ),

                    modifier = Modifier.weight(1f)

                ) {

                    tabs.forEachIndexed { index, tab ->

                        Tab(

                            selected =
                                index == safeIndex - 1,


                            onClick = {

                                val intent =
                                    Intent(
                                        context,
                                        TabRegistry.activityFor(
                                            index + 1
                                        )
                                    )


                                intent.putExtra(
                                    "tab_index",
                                    index + 1
                                )

                                intent.putExtra(
                                    "site_name",
                                    siteName
                                )


                                intent.putExtra(
                                    "site_url",
                                    tab.url
                                )


                                context.startActivity(
                                    intent
                                )
                            },

                            text = {

                                Text(
                                    tab.name
                                )
                            }
                        )
                    }

                }

            }
            // Botão Configurações (direita)
            IconButton(
                onClick = {

                    context.startActivity(

                        Intent(
                            context,
                            SiteSettingsActivity::class.java
                        )
                            .apply {

                                putExtra(
                                    "site_name",
                                    siteName
                                )

                                putExtra(
                                    "site_url",
                                    siteUrl
                                )
                            }
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Configurações"
                )
            }
        }


        // WEBVIEW: Ocupa o espaço restante
        AndroidView(

            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),



            factory = { ctx ->



                WebView(ctx)
                    .apply {

                        // Usar configuração centralizada
                        WebViewConfigManager.configureWebView(this)

                        webChromeClient =
                            object : WebChromeClient() {

                                override fun onPermissionRequest(
                                    request: PermissionRequest
                                ) {
                                    Log.d("WebView", "Permission request: ${request.resources.joinToString()}")
                                    request.grant(
                                        request.resources
                                    )
                                }

                                override fun onGeolocationPermissionsShowPrompt(
                                    origin: String?,
                                    callback: android.webkit.GeolocationPermissions.Callback?
                                ) {
                                    Log.d("WebView", "Geolocation permission: $origin")
                                    callback?.invoke(origin, true, true)
                                }
                            }



                        webViewClient =
                            object : WebViewClient() {

                                override fun shouldOverrideUrlLoading(
                                    view: WebView,
                                    request: android.webkit.WebResourceRequest
                                ): Boolean {
                                    Log.d("WebView", "URL loading: ${request.url}")
                                    return false
                                }

                                override fun onReceivedError(
                                    view: WebView?,
                                    request: android.webkit.WebResourceRequest?,
                                    error: android.webkit.WebResourceError?
                                ) {
                                    Log.e("WebView", "Error: ${error?.description} - Code: ${error?.errorCode}")
                                    super.onReceivedError(view, request, error)
                                }
                            }




                        webView =
                            this

                        loadUrl(
                            currentTab.url
                        )
                    }
            },



            update = { view ->


                if (
                    view.url != currentTab.url
                ) {

                    view.loadUrl(
                        currentTab.url
                    )
                } else {
                    // Injetar fixes quando atualizar URL
                    view.postDelayed({
                        WebViewConfigManager.injectOverlayFix(view)
                        WebViewConfigManager.injectCustomCSS(view)
                    }, 1000)
                }
            }
        )
    }
}
