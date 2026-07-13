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
    val tabIndex: Int,
    val tabs: List<TabItem> = emptyList()
)


object PrefsHelper {

    private const val PREFS_NAME = "tabs_prefs"
    private const val SITES_KEY = "sites"

    // Existe uma Activity/processo isolado (:tab1 a :tab15) pra cada slot -
    // ver TabRegistry.activityFor() e o AndroidManifest.xml.
    const val MAX_SITES = 15


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

                        // -1 = site salvo antes de existir esse campo (dado antigo)
                        tabIndex =
                            obj.optInt(
                                "tabIndex",
                                -1
                            ),

                        tabs = tabs
                    )
                )
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }


        // Migração/reparo: qualquer site sem slot válido (-1, fora de 1..15,
        // ou duplicado com outro site) recebe o menor slot livre. Isso cobre
        // tanto dados salvos antes desse campo existir quanto qualquer
        // inconsistência futura - cada site sempre acaba com um slot único,
        // que é o que garante processo/cookies/localStorage isolados de verdade.
        val usedSlots = mutableSetOf<Int>()
        var repaired = false

        val fixedResult = result.map { site ->
            if (site.tabIndex in 1..MAX_SITES && usedSlots.add(site.tabIndex)) {
                site
            } else {
                repaired = true
                val freeSlot = (1..MAX_SITES).firstOrNull { it !in usedSlots } ?: 1
                usedSlots.add(freeSlot)
                site.copy(tabIndex = freeSlot)
            }
        }

        if (repaired) {
            saveSites(context, fixedResult)
        }

        return fixedResult
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

            obj.put(
                "tabIndex",
                site.tabIndex
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




    /**
     * Adiciona um site, atribuindo o menor slot (1..MAX_SITES) ainda livre -
     * esse slot é o que define para sempre qual processo isolado (:tabN) o
     * site usa. Retorna false sem adicionar nada se os MAX_SITES slots já
     * estiverem todos ocupados.
     */
    fun addSite(
        context: Context,
        name: String,
        url: String
    ): Boolean {


        val sites =
            getSites(context)
                .toMutableList()


        val usedSlots =
            sites.map { it.tabIndex }.toSet()

        val freeSlot =
            (1..MAX_SITES).firstOrNull { it !in usedSlots }
                ?: return false



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
                tabIndex = freeSlot,

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

        return true
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