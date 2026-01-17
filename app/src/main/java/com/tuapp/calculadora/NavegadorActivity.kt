package com.tuapp.calculadora

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class NavegadorActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navegador)

        webView = findViewById(R.id.webView)
        val etUrl = findViewById<EditText>(R.id.etUrl)
        val btnIr = findViewById<Button>(R.id.btnIr)

        // Configuración profesional del navegador
        webView.webViewClient = WebViewClient() // Abre los links dentro de la app, no en Chrome
        val settings = webView.settings
        settings.javaScriptEnabled = true // Permite páginas modernas
        settings.domStorageEnabled = true // Permite que carguen bien los sitios

        webView.loadUrl("https://www.google.com")

        btnIr.setOnClickListener {
            val url = etUrl.text.toString()
            if (url.startsWith("http")) {
                webView.loadUrl(url)
            } else {
                webView.loadUrl("https://www.google.com/search?q=$url")
            }
        }
    }

    // Para que al tocar "atrás" no se cierre la app, sino que vuelva a la página anterior
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
