package com.tuapp.calculadora

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

class NavegadorActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navegador)

        webView = findViewById(R.id.webView)
        progressBar = findViewById(R.id.pbCarga)
        val etUrl = findViewById<EditText>(R.id.etUrl)
        val btnIr = findViewById<ImageButton>(R.id.btnIr)

        // Configuración moderna del WebView
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true // Para que carguen páginas modernas como YouTube
        webView.webViewClient = WebViewClient() // Para que los links abran dentro de la app

        // Lógica de la barra de carga
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                progressBar.progress = newProgress
                if (newProgress < 100) {
                    progressBar.visibility = View.VISIBLE
                } else {
                    progressBar.visibility = View.GONE
                }
            }
        }

        // Acción al presionar el botón de IR
        btnIr.setOnClickListener {
            cargarUrl(etUrl.text.toString())
        }

        // Acción al presionar "Enter" en el teclado
        etUrl.setOnEditorActionListener { _, _, _ ->
            cargarUrl(etUrl.text.toString())
            true
        }

        // Página de inicio por defecto
        webView.loadUrl("https://www.google.com")
    }

    private fun cargarUrl(url: String) {
        var urlFinal = url
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            urlFinal = "https://www.google.com/search?q=$url"
        }
        webView.loadUrl(urlFinal)
    }

    // Para que al dar "atrás" no se salga de la app, sino que regrese a la página anterior
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
