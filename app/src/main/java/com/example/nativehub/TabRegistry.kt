package com.example.nativehub

import android.app.Activity
import android.os.Build
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.webkit.WebViewCompat
import kotlin.reflect.KClass

/**
 * Registry que mapeia índices de abas para suas Activities.
 * Antes: 20 activities duplicadas (TabActivity2 até TabActivity20)
 * Depois: Uma única GenericTabActivity reutilizável
 */
object TabRegistry {

    /**
     * Retorna a Activity apropriada para o índice da aba.
     * Todas as abas agora usam GenericTabActivity
     */
    fun activityFor(tabIndex: Int): Class<out ComponentActivity> {
        return when (tabIndex) {
            1 -> EightUActivity::class.java
            else -> GenericTabActivity::class.java
        }
    }

    /**
     * Cria um Intent para a aba com todas as configurações necessárias
     */
    fun createTabIntent(
        context: android.content.Context,
        tabIndex: Int,
        siteName: String,
        siteUrl: String
    ): android.content.Intent {
        return android.content.Intent(context, activityFor(tabIndex)).apply {
            putExtra("tab_index", tabIndex)
            putExtra("site_name", siteName)
            putExtra("site_url", siteUrl)
        }
    }
}

/**
 * Utilitário para configuração avançada da WebView
 * Centraliza todas as configurações em um único lugar
 */
object WebViewConfigManager {

    /**
     * Configura uma WebView com todas as settings otimizadas
     */
    fun configureWebView(webView: WebView) {
        webView.settings.apply {
            // JavaScript
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true

            // Storage
            domStorageEnabled = true
            databaseEnabled = true
            allowFileAccess = true
            allowContentAccess = true

            // Mídia
            loadsImagesAutomatically = true
            mediaPlaybackRequiresUserGesture = false

            // Renderização
            useWideViewPort = true
            loadWithOverviewMode = true

            // Múltiplas janelas
            setSupportMultipleWindows(true)

            // Mixed Content (para suportar http em https)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }

            // User Agent padrão
            userAgentString = "NativeHub/1.0 (Android)"

            // Cache
            cacheMode = WebSettings.LOAD_DEFAULT
            databasePath = webView.context.getDir("database", android.content.Context.MODE_PRIVATE).path
        }
    }

    /**
     * Injeta JavaScript para corrigir problemas com overlay travado
     */
    fun injectOverlayFix(webView: WebView) {
        val fixScript = """
            (function() {
                // Remover overlays que ficam travados
                const overlays = document.querySelectorAll('.van-overlay, [role="presentation"]');
                overlays.forEach(overlay => {
                    if (window.getComputedStyle(overlay).pointerEvents === 'auto' && 
                        overlay.style.backgroundColor && 
                        overlay.style.backgroundColor.includes('rgba(0,0,0')) {
                        overlay.style.pointerEvents = 'none';
                        console.log('[NativeHub] Overlay fixed: ' + overlay.className);
                    }
                });
                
                // Garantir que popups possam ser interagidos
                const popups = document.querySelectorAll('[role="dialog"], .van-popup, .modal, [class*="popup"]');
                popups.forEach(popup => {
                    popup.style.zIndex = '9999';
                });
                
                // Observer para monitorar mudanças contínuas
                const observer = new MutationObserver(() => {
                    const overlay = document.querySelector('.van-overlay');
                    if (overlay && overlay.style.pointerEvents !== 'none' && 
                        overlay.style.backgroundColor && 
                        overlay.style.backgroundColor.includes('rgba(0,0,0')) {
                        overlay.style.pointerEvents = 'none';
                    }
                });
                
                observer.observe(document.body, {
                    attributes: true,
                    subtree: true,
                    attributeFilter: ['style', 'class']
                });
                
                console.log('[NativeHub] Overlay fix injected and monitored');
            })();
        """.trimIndent()

        webView.evaluateJavascript(fixScript) { result ->
            android.util.Log.d("WebViewFix", "Overlay fix result: $result")
        }
    }

    /**
     * Injeta CSS para melhorar o comportamento da página
     */
    fun injectCustomCSS(webView: WebView) {
        val cssScript = """
            (function() {
                const style = document.createElement('style');
                style.innerHTML = `
                    .van-overlay {
                        pointer-events: none !important;
                    }
                    [role="dialog"] {
                        z-index: 9999 !important;
                    }
                    input, button, select, textarea {
                        -webkit-touch-callout: auto;
                        -webkit-user-select: text;
                    }
                `;
                document.head.appendChild(style);
                console.log('[NativeHub] Custom CSS injected');
            })();
        """.trimIndent()

        webView.evaluateJavascript(cssScript) { result ->
            android.util.Log.d("WebViewCSS", "CSS injection result: $result")
        }
    }
}
