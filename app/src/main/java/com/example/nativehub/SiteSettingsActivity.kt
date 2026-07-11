package com.example.nativehub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

class SiteSettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val siteName =
            intent.getStringExtra("site_name") ?: ""

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    SiteSettingsScreen(siteName)
                }
            }
        }
    }
}

@Composable
private fun SiteSettingsScreen(
    siteName: String
) {

    val context = LocalContext.current

    var sites by remember {
        mutableStateOf(
            PrefsHelper.getSites(context)
        )
    }

    val site =
        sites.firstOrNull {
            it.name == siteName
        }

    var showDialog by remember {
        mutableStateOf(false)
    }

    var editingIndex by remember {
        mutableStateOf<Int?>(null)
    }

    var tabName by remember {
        mutableStateOf("")
    }

    var tabUrl by remember {
        mutableStateOf("")
    }

    if (site == null) {

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Text("Site não encontrado")
        }

        return
    }

    if (showDialog) {

        AlertDialog(
            onDismissRequest = {
                showDialog = false
                editingIndex = null
            },

            title = {
                Text(
                    if (editingIndex == null)
                        "Adicionar Aba"
                    else
                        "Editar Aba"
                )
            },

            text = {

                Column {

                    OutlinedTextField(
                        value = tabName,
                        onValueChange = {
                            tabName = it
                        },
                        label = {
                            Text("Nome da Aba")
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    OutlinedTextField(
                        value = tabUrl,
                        onValueChange = {
                            tabUrl = it
                        },
                        label = {
                            Text("URL")
                        }
                    )
                }
            },

            confirmButton = {

                Button(
                    onClick = {

                        if (
                            tabName.isBlank() ||
                            tabUrl.isBlank()
                        ) return@Button

                        var finalUrl =
                            tabUrl.trim()

                        if (
                            !finalUrl.startsWith("http://") &&
                            !finalUrl.startsWith("https://")
                        ) {
                            finalUrl =
                                "https://$finalUrl"
                        }

                        val updated =
                            sites.map { currentSite ->

                                if (
                                    currentSite.name != siteName
                                ) {
                                    currentSite
                                } else {

                                    if (editingIndex == null) {

                                        currentSite.copy(
                                            tabs =
                                                currentSite.tabs + TabItem(
                                                    tabName,
                                                    finalUrl
                                                )
                                        )

                                    } else {

                                        currentSite.copy(
                                            tabs =
                                                currentSite.tabs.mapIndexed { index, tab ->

                                                    if (
                                                        index == editingIndex
                                                    ) {
                                                        TabItem(
                                                            tabName,
                                                            finalUrl
                                                        )
                                                    } else {
                                                        tab
                                                    }
                                                }
                                        )
                                    }
                                }
                            }

                        PrefsHelper.saveSites(
                            context,
                            updated
                        )

                        sites =
                            PrefsHelper.getSites(context)

                        tabName = ""
                        tabUrl = ""
                        editingIndex = null
                        showDialog = false
                    }
                ) {
                    Text("Salvar")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = site.name,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Button(
            onClick = {

                editingIndex = null
                tabName = ""
                tabUrl = ""
                showDialog = true
            }
        ) {
            Text("+ Adicionar Aba")
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        LazyColumn {

            itemsIndexed(site.tabs) { index, tab ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(tab.name)

                        Text(
                            tab.url,
                            style =
                                MaterialTheme.typography.bodySmall
                        )
                    }

                    Row {

                        if (index > 0) {

                            Button(
                                onClick = {

                                    val updated =
                                        sites.map { s ->

                                            if (
                                                s.name != siteName
                                            ) {
                                                s
                                            } else {

                                                val tabs =
                                                    s.tabs.toMutableList()

                                                val temp =
                                                    tabs[index]

                                                tabs[index] =
                                                    tabs[index - 1]

                                                tabs[index - 1] =
                                                    temp

                                                s.copy(
                                                    tabs = tabs
                                                )
                                            }
                                        }

                                    PrefsHelper.saveSites(
                                        context,
                                        updated
                                    )

                                    sites =
                                        PrefsHelper.getSites(context)
                                }
                            ) {
                                Text("↑")
                            }
                        }

                        if (
                            index <
                            site.tabs.lastIndex
                        ) {

                            Button(
                                onClick = {

                                    val updated =
                                        sites.map { s ->

                                            if (
                                                s.name != siteName
                                            ) {
                                                s
                                            } else {

                                                val tabs =
                                                    s.tabs.toMutableList()

                                                val temp =
                                                    tabs[index]

                                                tabs[index] =
                                                    tabs[index + 1]

                                                tabs[index + 1] =
                                                    temp

                                                s.copy(
                                                    tabs = tabs
                                                )
                                            }
                                        }

                                    PrefsHelper.saveSites(
                                        context,
                                        updated
                                    )

                                    sites =
                                        PrefsHelper.getSites(context)
                                }
                            ) {
                                Text("↓")
                            }
                        }

                        Button(
                            onClick = {

                                editingIndex =
                                    index

                                tabName =
                                    tab.name

                                tabUrl =
                                    tab.url

                                showDialog =
                                    true
                            }
                        ) {
                            Text("Editar")
                        }

                        if (index != 0) {

                            Button(
                                onClick = {

                                    val updated =
                                        sites.map {

                                            if (
                                                it.name != siteName
                                            ) {
                                                it
                                            } else {

                                                it.copy(
                                                    tabs =
                                                        it.tabs.filterIndexed { i, _ ->
                                                            i != index
                                                        }
                                                )
                                            }
                                        }

                                    PrefsHelper.saveSites(
                                        context,
                                        updated
                                    )

                                    sites =
                                        PrefsHelper.getSites(
                                            context
                                        )
                                }
                            ) {
                                Text("Remover")
                            }
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }
        }
    }
}