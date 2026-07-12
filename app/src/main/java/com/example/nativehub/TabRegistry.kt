@file:Suppress("DEPRECATION")

package com.example.nativehub

import android.util.Log
import android.webkit.WebSettings
import android.webkit.WebView

/**
 * Registry que mapeia índices de abas para suas Activities.
 * Cada Activity abaixo tem um android:process próprio (:tab1 a :tab15)
 * declarado no AndroidManifest.xml, garantindo processo, cookies, cache,
 * LocalStorage e sessão independentes por aba.
 */
object TabRegistry {

    /**
     * Retorna a Activity (e, portanto, o processo) correspondente ao índice da aba.
     * tabIndex fora do intervalo 1-15 cai no fallback (GenericTabActivity / :tab2).
     */
    fun activityFor(tabIndex: Int): Class<out androidx.activity.ComponentActivity> {
        return when (tabIndex) {
            1 -> EightUActivity::class.java
            2 -> GenericTabActivity::class.java
            3 -> Tab3Activity::class.java
            4 -> Tab4Activity::class.java
            5 -> Tab5Activity::class.java
            6 -> Tab6Activity::class.java
            7 -> Tab7Activity::class.java
            8 -> Tab8Activity::class.java
            9 -> Tab9Activity::class.java
            10 -> Tab10Activity::class.java
            11 -> Tab11Activity::class.java
            12 -> Tab12Activity::class.java
            13 -> Tab13Activity::class.java
            14 -> Tab14Activity::class.java
            15 -> Tab15Activity::class.java
            else -> GenericTabActivity::class.java
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
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

            // User Agent padrão
            userAgentString = "NativeHub/1.0 (Android)"

            // Cache
            cacheMode = WebSettings.LOAD_DEFAULT
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
            Log.d("WebViewFix", "Overlay fix result: $result")
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
            Log.d("WebViewCSS", "CSS injection result: $result")
        }
    }
}
