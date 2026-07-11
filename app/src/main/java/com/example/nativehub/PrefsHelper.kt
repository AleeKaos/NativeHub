package com.example.nativehub

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject


data class TabItem(
    val name: String,
    val url: String
)


data class Site(
    val name: String,
    val url: String,
    val tabs: List<TabItem> = emptyList()
)


object PrefsHelper {

    private const val PREFS_NAME = "tabs_prefs"
    private const val SITES_KEY = "sites"


    fun saveName(
        context: Context,
        tabIndex: Int,
        name: String
    ) {

        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
            .edit()
            .putString(
                keyFor(tabIndex),
                name
            )
            .commit()
    }


    fun getName(
        context: Context,
        tabIndex: Int
    ): String {

        val default =
            "Aba $tabIndex"

        return context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
            .getString(
                keyFor(tabIndex),
                default
            )
            ?: default
    }


    private fun keyFor(
        tabIndex: Int
    ) =
        "tab_$tabIndex"



    fun getSites(
        context: Context
    ): List<Site> {


        val prefs =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )


        val json =
            prefs.getString(
                SITES_KEY,
                "[]"
            )
                ?: "[]"


        val result =
            mutableListOf<Site>()


        try {

            val array =
                JSONArray(json)


            for (i in 0 until array.length()) {


                val obj =
                    array.getJSONObject(i)


                val tabs =
                    mutableListOf<TabItem>()


                val tabsArray =
                    obj.optJSONArray(
                        "tabs"
                    )
                        ?: JSONArray()



                for (j in 0 until tabsArray.length()) {


                    val tabObj =
                        tabsArray.getJSONObject(j)


                    tabs.add(
                        TabItem(
                            name =
                                tabObj.optString(
                                    "name"
                                ),

                            url =
                                tabObj.optString(
                                    "url"
                                )
                        )
                    )
                }



                result.add(
                    Site(
                        name =
                            obj.optString(
                                "name"
                            ),

                        url =
                            obj.optString(
                                "url"
                            ),

                        tabs = tabs
                    )
                )
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }


        return result
    }



    fun saveSites(
        context: Context,
        sites: List<Site>
    ) {


        val array =
            JSONArray()



        sites.forEach { site ->


            val obj =
                JSONObject()


            obj.put(
                "name",
                site.name
            )


            obj.put(
                "url",
                site.url
            )



            val tabsArray =
                JSONArray()



            site.tabs.forEach { tab ->


                val tabObj =
                    JSONObject()


                tabObj.put(
                    "name",
                    tab.name
                )


                tabObj.put(
                    "url",
                    tab.url
                )


                tabsArray.put(
                    tabObj
                )
            }



            obj.put(
                "tabs",
                tabsArray
            )


            array.put(
                obj
            )
        }



        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
            .edit()
            .putString(
                SITES_KEY,
                array.toString()
            )
            .commit()
    }




    fun addSite(
        context: Context,
        name: String,
        url: String
    ) {


        val sites =
            getSites(context)
                .toMutableList()



        var finalUrl =
            url.trim()



        if (
            !finalUrl.startsWith("http://") &&
            !finalUrl.startsWith("https://")
        ) {

            finalUrl =
                "https://$finalUrl"
        }



        sites.add(
            Site(
                name = name,
                url = finalUrl,

                tabs = listOf(
                    TabItem(
                        name = "Principal",
                        url = finalUrl
                    )
                )
            )
        )



        saveSites(
            context,
            sites
        )
    }





    fun removeSite(
        context: Context,
        index: Int
    ) {


        val sites =
            getSites(context)
                .toMutableList()



        if (
            index in sites.indices
        ) {


            sites.removeAt(index)



            saveSites(
                context,
                sites
            )
        }
    }
}