package com.example.nativehub

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class EightUActivity : ComponentActivity() {

    // Por que mutableStateOf (e não uma val simples) aqui:
    // Esta Activity usa launchMode="singleTask", então onNewIntent() é chamado
    // reaproveitando a instância já existente, SEM que onCreate() (e portanto
    // setContent{}) rode de novo. Se os valores fossem apenas extraídos do intent
    // dentro de onNewIntent() sem esse estado observável, a composição já montada
    // nunca saberia que os dados mudaram e a tela continuaria com o site antigo.
    // mutableStateOf permite que onNewIntent() atualize esses valores e o Compose
    // recomponha automaticamente o TabScreen já existente com os dados novos —
    // sem destruir/recriar a Activity nem a WebView (evita reload da página e
    // perda de sessão/scroll que aconteceria com recreate() ou com um novo
    // setContent{}). Nenhuma alteração de UI/estética foi feita: é só o
    // "encanamento" entre onNewIntent() e o TabScreen já existente.
    private var siteNameState by mutableStateOf("")
    private var siteUrlState by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        applyIntentExtras(intent)

        setContent {
            TabScreen(
                tabIndex = 1,
                siteName = siteNameState,
                siteUrl = siteUrlState
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        applyIntentExtras(intent)
    }

    private fun applyIntentExtras(intent: Intent) {
        siteNameState = intent.getStringExtra("site_name") ?: ""
        siteUrlState = intent.getStringExtra("site_url") ?: ""
    }
}