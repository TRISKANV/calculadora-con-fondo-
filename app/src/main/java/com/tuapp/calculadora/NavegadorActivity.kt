package com.tuapp.calculadora

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

class NavegadorActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navegador)

        webView = findViewById(R.id.webView)
        val etUrl = findViewById<EditText>(R.id.etUrl)
        val btnIr = findViewById<ImageButton>(R.id.btnIr)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        // Configuración de Privacidad y Rendimiento
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.webViewClient = WebViewClient() 
        
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progressBar.visibility = if (newProgress < 100) android.view.View.VISIBLE else android.view.View.GONE
                progressBar.progress = newProgress
            }
        }

        // Cargar Google por defecto
        webView.loadUrl("https://www.google.com")

        btnIr.setOnClickListener {
            var url = etUrl.text.toString()
            if (!url.startsWith("http")) url = "https://www.google.com/search?q=$url"
            webView.loadUrl(url)
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }
}
