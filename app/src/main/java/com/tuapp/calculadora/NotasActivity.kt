package com.tuapp.calculadora

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.webkit.*
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

        // --- OPTIMIZACIÓN DE WEBVIEW ---
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            cacheMode = WebSettings.LOAD_DEFAULT
        }
        webView.webViewClient = WebViewClient()

        // --- SELECTOR DE MOTORES (Texto por ahora) ---
        val motores = arrayOf("Google", "DuckDuckGo", "StartPage", "Mojeek")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, motores)
        spinnerMotor.adapter = adapter

        spinnerMotor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                motorSeleccionado = when (position) {
                    1 -> "https://duckduckgo.com/?q="
                    2 -> "https://www.startpage.com/do/search?query="
                    3 -> "https://www.mojeek.com/search?q="
                    else -> "https://www.google.com/search?q="
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // --- MEJORA: BOTÓN ENTER DEL TECLADO ---
        etUrl.setOnEditorActionListener { _, actionId, _ ->
            // Detecta "Ir", "Buscar" o "Listo" en el teclado
            if (actionId == EditorInfo.IME_ACTION_GO || 
                actionId == EditorInfo.IME_ACTION_SEARCH || 
                actionId == EditorInfo.IME_ACTION_DONE) {
                ejecutarBusqueda(etUrl.text.toString())
                true
            } else {
                false
            }
        }

        btnIr.setOnClickListener { ejecutarBusqueda(etUrl.text.toString()) }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progressBar.visibility = if (newProgress < 100) android.view.View.VISIBLE else android.view.View.GONE
                progressBar.progress = newProgress
            }
        }
    }

    private fun ejecutarBusqueda(entrada: String) {
        if (entrada.isEmpty()) return
        val url = entrada.trim()
        
        if (url.startsWith("http")) {
            webView.loadUrl(url)
        } else if (url.contains(".") && !url.contains(" ")) {
            webView.loadUrl("https://$url")
        } else {
            webView.loadUrl(motorSeleccionado + url)
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }
}
