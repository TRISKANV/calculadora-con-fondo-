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
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.cacheMode = WebSettings.LOAD_DEFAULT
        webView.webViewClient = WebViewClient()

        // --- MEJORA 1: LOGOS DINÁMICOS ---
        val motores = arrayOf("Google", "DuckDuckGo", "StartPage", "Mojeek")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, motores)
        spinnerMotor.adapter = adapter

        spinnerMotor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                when (motores[position]) {
                    "DuckDuckGo" -> {
                        motorSeleccionado = "https://duckduckgo.com/?q="
                        // Aquí podrías cambiar el ícono si tuvieras los drawables
                        // btnIr.setImageResource(R.drawable.logo_duck) 
                    }
                    "StartPage" -> motorSeleccionado = "https://www.startpage.com/do/search?query="
                    "Mojeek" -> motorSeleccionado = "https://www.mojeek.com/search?q="
                    else -> motorSeleccionado = "https://www.google.com/search?q="
                }
                Toast.makeText(this@NavegadorActivity, "Motor: ${motores[position]}", Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // --- MEJORA 2: BOTÓN ENTER DEL TECLADO ---
        etUrl.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE) {
                ejecutarBusqueda(etUrl.text.toString())
                true
            } else {
                false
            }
        }

        btnIr.setOnClickListener {
            ejecutarBusqueda(etUrl.text.toString())
        }
    }

    private fun ejecutarBusqueda(entrada: String) {
        var url = entrada
        if (url.isEmpty()) return
        
        if (url.startsWith("http://") || url.startsWith("https://")) {
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
