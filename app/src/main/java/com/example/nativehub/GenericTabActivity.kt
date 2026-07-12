package com.example.nativehub

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.webkit.WebSettings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Activity genérica para as abas 2 em diante.
 * Cada aba roda em processo separado (:tab2, :tab3 ... :tab15), definido no
 * AndroidManifest.xml através das subclasses finas declaradas abaixo. A subclasse
 * não adiciona nenhum comportamento novo: existe apenas para dar a cada aba um
 * android:name próprio no Manifest, permitindo declarar um android:process próprio.
 *
 * "open" para permitir a herança pelas subclasses de processo (Tab3Activity..Tab15Activity).
 */
open class GenericTabActivity : ComponentActivity() {

    // Por que mutableStateOf (e não uma val simples) aqui:
    // Esta Activity usa launchMode="singleTask", então onNewIntent() é chamado
    // reaproveitando a instância já existente, SEM que onCreate() (e portanto
    // setContent{}) rode de novo. Se os valores fossem apenas extraídos do intent
    // dentro de onNewIntent() sem esse estado observável, a composição já montada
    // nunca saberia que os dados mudaram e a tela continuaria com o tab/site antigo.
    // mutableStateOf permite que onNewIntent() atualize esses valores e o Compose
    // recomponha automaticamente o TabScreen já existente com os dados novos —
    // sem destruir/recriar a Activity nem a WebView (evita reload da página e
    // perda de sessão/scroll que aconteceria com recreate() ou com um novo
    // setContent{}). Nenhuma alteração de UI/estética foi feita: é só o
    // "encanamento" entre onNewIntent() e o TabScreen já existente.
    private var tabIndexState by mutableStateOf(1)
    private var siteNameState by mutableStateOf("")
    private var siteUrlState by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        applyIntentExtras(intent)

        Log.d(
            "GenericTabActivity",
            "onCreate - Tab $tabIndexState - Site: $siteNameState - URL: $siteUrlState"
        )

        setContent {
            TabScreen(
                tabIndex = tabIndexState,
                siteName = siteNameState,
                siteUrl = siteUrlState
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        // Garante que getIntent() futuro reflita o intent mais recente
        setIntent(intent)

        applyIntentExtras(intent)

        Log.d(
            "GenericTabActivity",
            "onNewIntent - Tab $tabIndexState - Site: $siteNameState - URL: $siteUrlState"
        )
    }

    private fun applyIntentExtras(intent: Intent) {
        tabIndexState = intent.getIntExtra("tab_index", 1)
        siteNameState = intent.getStringExtra("site_name") ?: ""
        siteUrlState = intent.getStringExtra("site_url") ?: ""
    }
}

/**
 * Subclasses finas usadas exclusivamente para isolamento de processo por aba.
 * Nenhuma reimplementa lógica: todo o comportamento vem de GenericTabActivity.
 * A única diferença entre elas é o android:process declarado no AndroidManifest.xml
 * para cada android:name correspondente.
 */
class Tab3Activity : GenericTabActivity()
class Tab4Activity : GenericTabActivity()
class Tab5Activity : GenericTabActivity()
class Tab6Activity : GenericTabActivity()
class Tab7Activity : GenericTabActivity()
class Tab8Activity : GenericTabActivity()
class Tab9Activity : GenericTabActivity()
class Tab10Activity : GenericTabActivity()
class Tab11Activity : GenericTabActivity()
class Tab12Activity : GenericTabActivity()
class Tab13Activity : GenericTabActivity()
class Tab14Activity : GenericTabActivity()
class Tab15Activity : GenericTabActivity()
