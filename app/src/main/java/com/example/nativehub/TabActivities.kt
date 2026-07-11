package com.example.nativehub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

object TabRegistry {

    fun activityFor(tabIndex: Int): Class<out ComponentActivity> =
        when (tabIndex) {
            1 -> EightUActivity::class.java
            2 -> TabActivity2::class.java
            3 -> TabActivity3::class.java
            4 -> TabActivity4::class.java
            5 -> TabActivity5::class.java
            6 -> TabActivity6::class.java
            7 -> TabActivity7::class.java
            8 -> TabActivity8::class.java
            9 -> TabActivity9::class.java
            10 -> TabActivity10::class.java
            11 -> TabActivity11::class.java
            12 -> TabActivity12::class.java
            13 -> TabActivity13::class.java
            14 -> TabActivity14::class.java
            15 -> TabActivity15::class.java
            16 -> TabActivity16::class.java
            17 -> TabActivity17::class.java
            18 -> TabActivity18::class.java
            19 -> TabActivity19::class.java
            20 -> TabActivity20::class.java
            else -> EightUActivity::class.java
        }
}

abstract class BaseTabActivity(
    private val tabIndexValue: Int
) : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val siteName =
            intent.getStringExtra("site_name") ?: ""

        val siteUrl =
            intent.getStringExtra("site_url") ?: ""

        setContent {
            TabScreen(
                tabIndex = tabIndexValue,
                siteName = siteName,
                siteUrl = siteUrl
            )
        }
    }
}

class TabActivity2 : BaseTabActivity(2)
class TabActivity3 : BaseTabActivity(3)
class TabActivity4 : BaseTabActivity(4)
class TabActivity5 : BaseTabActivity(5)
class TabActivity6 : BaseTabActivity(6)
class TabActivity7 : BaseTabActivity(7)
class TabActivity8 : BaseTabActivity(8)
class TabActivity9 : BaseTabActivity(9)
class TabActivity10 : BaseTabActivity(10)
class TabActivity11 : BaseTabActivity(11)
class TabActivity12 : BaseTabActivity(12)
class TabActivity13 : BaseTabActivity(13)
class TabActivity14 : BaseTabActivity(14)
class TabActivity15 : BaseTabActivity(15)
class TabActivity16 : BaseTabActivity(16)
class TabActivity17 : BaseTabActivity(17)
class TabActivity18 : BaseTabActivity(18)
class TabActivity19 : BaseTabActivity(19)
class TabActivity20 : BaseTabActivity(20)