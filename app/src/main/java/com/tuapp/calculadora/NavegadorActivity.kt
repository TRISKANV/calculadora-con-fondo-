package com.tuapp.calculadora

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class NavegadorActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private var motorSeleccionado = "https://www.google.com/search?q="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navegador)

        webView = findViewById(R.id.webView)
        val etUrl = findViewById<EditText>(R.id.etUrl)
        val btnIr = findViewById<ImageButton>(R.id.btnIr)
        val spinnerMotor = findViewById<Spinner>(R.id.spinnerMotor)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        // Configuración del Selector de Motores
        val motores = arrayOf("Google", "DuckDuckGo", "StartPage", "Mojeek")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, motores)
        spinnerMotor.adapter = adapter

        spinnerMotor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                motorSeleccionado = when (motores[position]) {
                    "DuckDuckGo" -> "https://duckduckgo.com/?q="
                    "StartPage" -> "https://www.startpage.com/do/search?query="
                    "Mojeek" -> "https://www.mojeek.com/search?q="
                    else -> "https://www.google.com/search?q="
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Configuración del WebView (Privacidad)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progressBar.visibility = if (newProgress < 100) android.view.View.VISIBLE else android.view.View.GONE
                progressBar.progress = newProgress
            }
        }

        btnIr.setOnClickListener {
            var url = etUrl.text.toString()
            if (url.startsWith("http")) {
                webView.loadUrl(url)
            } else {
                webView.loadUrl(motorSeleccionado + url)
            }
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }
}
