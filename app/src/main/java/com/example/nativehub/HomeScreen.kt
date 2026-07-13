package com.example.nativehub

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect

@Composable
fun HomeScreen() {

    val context = LocalContext.current

    var sites by remember {
        mutableStateOf(
            PrefsHelper.getSites(context)
        )
    }

    LifecycleEventEffect(
        Lifecycle.Event.ON_RESUME
    ) {
        sites = PrefsHelper.getSites(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "NativeHub",
            style = MaterialTheme.typography.headlineMedium
        )

        sites.forEach { site ->

            Button(
                onClick = {

                    context.startActivity(
                        Intent(
                            context,
                            TabRegistry.activityFor(site.tabIndex)
                        ).apply {

                            putExtra(
                                "tab_index",
                                site.tabIndex
                            )

                            putExtra(
                                "site_name",
                                site.name
                            )

                            putExtra(
                                "site_url",
                                site.url
                            )
                        }
                    )
                }
            ) {
                Text(site.name)
            }
        }

        Button(
            onClick = {

                context.startActivity(
                    Intent(
                        context,
                        SettingsActivity::class.java
                    )
                )
            }
        ) {
            Text("⚙ Configurações")
        }
    }
}