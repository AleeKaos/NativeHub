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


class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            MaterialTheme {

                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {

                    SettingsScreen()
                }
            }
        }
    }
}


@Composable
private fun SettingsScreen() {

    val context = LocalContext.current


    var sites by remember {
        mutableStateOf(
            PrefsHelper.getSites(context)
        )
    }


    var showDialog by remember {
        mutableStateOf(false)
    }


    var editIndex by remember {
        mutableStateOf(-1)
    }


    var siteName by remember {
        mutableStateOf("")
    }


    var siteUrl by remember {
        mutableStateOf("")
    }



    fun reload() {

        sites =
            PrefsHelper.getSites(context)
    }



    if (showDialog) {


        AlertDialog(

            onDismissRequest = {
                showDialog = false
            },


            title = {

                Text(
                    if (editIndex == -1)
                        "Adicionar Site"
                    else
                        "Editar Site"
                )
            },


            text = {

                Column {


                    OutlinedTextField(

                        value = siteName,

                        onValueChange = {
                            siteName = it
                        },

                        label = {
                            Text("Nome")
                        },

                        modifier =
                            Modifier.fillMaxWidth()
                    )


                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )


                    OutlinedTextField(

                        value = siteUrl,

                        onValueChange = {
                            siteUrl = it
                        },

                        label = {
                            Text("URL")
                        },

                        modifier =
                            Modifier.fillMaxWidth()
                    )
                }
            },


            confirmButton = {


                Button(

                    onClick = {


                        if (
                            siteName.isBlank() ||
                            siteUrl.isBlank()
                        ) return@Button



                        var finalUrl =
                            siteUrl.trim()



                        if (
                            !finalUrl.startsWith("http://") &&
                            !finalUrl.startsWith("https://")
                        ) {

                            finalUrl =
                                "https://$finalUrl"
                        }



                        val updated =
                            sites.toMutableList()



                        if (editIndex == -1) {


                            PrefsHelper.addSite(
                                context,
                                siteName,
                                finalUrl
                            )


                        } else {


                            val old =
                                updated[editIndex]


                            updated[editIndex] =
                                old.copy(
                                    name = siteName,
                                    url = finalUrl
                                )


                            PrefsHelper.saveSites(
                                context,
                                updated
                            )
                        }



                        reload()



                        siteName = ""
                        siteUrl = ""

                        editIndex = -1

                        showDialog = false
                    }

                ) {

                    Text("Salvar")
                }
            },


            dismissButton = {


                TextButton(

                    onClick = {

                        showDialog = false

                        editIndex = -1
                    }

                ) {

                    Text("Cancelar")
                }
            }
        )
    }



    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp)

    ) {


        Text(

            text = "Sites",

            style =
                MaterialTheme.typography.titleLarge
        )



        Button(

            onClick = {

                siteName = ""
                siteUrl = ""

                editIndex = -1

                showDialog = true
            },

            modifier =
                Modifier.padding(top = 12.dp)

        ) {

            Text("+ Adicionar Site")
        }




        LazyColumn(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)

        ) {



            itemsIndexed(sites) { index, site ->



                Column(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)

                ) {


                    Text(
                        text = site.name
                    )


                    Text(
                        text = site.url,
                        style =
                            MaterialTheme.typography.bodySmall
                    )


                    Row(

                        horizontalArrangement =
                            Arrangement.spacedBy(4.dp)

                    ) {



                        Button(

                            enabled = index > 0,

                            onClick = {


                                val list =
                                    sites.toMutableList()


                                val temp =
                                    list[index - 1]


                                list[index - 1] =
                                    list[index]


                                list[index] =
                                    temp



                                PrefsHelper.saveSites(
                                    context,
                                    list
                                )


                                reload()
                            }

                        ) {

                            Text("↑")
                        }




                        Button(

                            enabled =
                                index < sites.size - 1,


                            onClick = {


                                val list =
                                    sites.toMutableList()


                                val temp =
                                    list[index + 1]


                                list[index + 1] =
                                    list[index]


                                list[index] =
                                    temp



                                PrefsHelper.saveSites(
                                    context,
                                    list
                                )


                                reload()

                            }

                        ) {

                            Text("↓")
                        }





                        Button(

                            onClick = {


                                siteName =
                                    site.name


                                siteUrl =
                                    site.url


                                editIndex =
                                    index


                                showDialog = true

                            }

                        ) {

                            Text("Editar")
                        }





                        Button(

                            onClick = {


                                PrefsHelper.removeSite(
                                    context,
                                    index
                                )


                                reload()

                            }

                        ) {

                            Text("Remover")
                        }
                    }
                }
            }
        }
    }
}