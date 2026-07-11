package com.example.nativehub

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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

        if (
            webView?.canGoBack() == true
        ) {

            webView?.goBack()

        } else {

            (context as? Activity)
                ?.finish()
        }
    }



    Column(
        modifier =
            Modifier.fillMaxSize()
    ) {



        ScrollableTabRow(

            selectedTabIndex =
                (safeIndex - 1)
                    .coerceAtMost(
                        (tabs.size - 1)
                            .coerceAtLeast(0)
                    )

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



            Tab(

                selected = false,


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
                },


                text = {

                    Text("?")
                }
            )
        }





        AndroidView(

            modifier =
                Modifier.fillMaxSize(),



            factory = { ctx ->



                WebView(ctx)
                    .apply {



                        settings.javaScriptEnabled =
                            true


                        settings.domStorageEnabled =
                            true


                        settings.databaseEnabled =
                            true


                        settings.allowFileAccess =
                            true


                        settings.allowContentAccess =
                            true


                        settings.javaScriptCanOpenWindowsAutomatically =
                            true


                        settings.setSupportMultipleWindows(
                            true
                        )


                        settings.loadsImagesAutomatically =
                            true


                        settings.mediaPlaybackRequiresUserGesture =
                            false


                        settings.useWideViewPort =
                            true


                        settings.loadWithOverviewMode =
                            true



                        webChromeClient =
                            object : WebChromeClient() {

                                override fun onPermissionRequest(
                                    request: PermissionRequest
                                ) {

                                    request.grant(
                                        request.resources
                                    )
                                }
                            }



                        webViewClient =
                            object : WebViewClient() {


                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    url: String?
                                ): Boolean {

                                    return false
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
                }
            }
        )
    }
}