package com.tuapp.calculadora

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

// Clase para definir cada motor de búsqueda
data class MotorBusqueda(val nombre: String, val url: String, val icono: Int)

class NavegadorActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private var motorActualUrl = "https://www.google.com/search?q="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navegador)

        webView = findViewById(R.id.webView)
        progressBar = findViewById(R.id.pbCarga)
        val etUrl = findViewById<EditText>(R.id.etUrl)
        val btnIr = findViewById<ImageButton>(R.id.btnIr)
        val spinnerMotores = findViewById<Spinner>(R.id.spinnerMotores)

        // 1. Configurar la lista de motores de búsqueda
        val listaMotores = listOf(
            MotorBusqueda("Google", "https://www.google.com/search?q=", android.R.drawable.ic_menu_search),
            MotorBusqueda("DuckDuckGo", "https://duckduckgo.com/?q=", android.R.drawable.ic_menu_view),
            MotorBusqueda("Mojeek", "https://www.mojeek.com/search?q=", android.R.drawable.ic_menu_compass),
            MotorBusqueda("Searx", "https://searx.be/search?q=", android.R.drawable.ic_menu_manage)
        )

        // 2. Configurar el Adaptador Personalizado para el Spinner
        val adapter = MotorAdapter(this, listaMotores)
        spinnerMotores.adapter = adapter

        // 3. Detectar qué motor selecciona el usuario
        spinnerMotores.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                motorActualUrl = listaMotores[position].url
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Configuración del WebView
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true 
        webView.webViewClient = WebViewClient() 

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                progressBar.progress = newProgress
                progressBar.visibility = if (newProgress < 100) View.VISIBLE else View.GONE
            }
        }

        btnIr.setOnClickListener { cargarUrl(etUrl.text.toString()) }

        etUrl.setOnEditorActionListener { _, _, _ ->
            cargarUrl(etUrl.text.toString())
            true
        }

        webView.loadUrl("https://www.google.com")
    }

    private fun cargarUrl(url: String) {
        val urlFinal = if (!url.startsWith("http://") && !url.startsWith("https://")) {
            motorActualUrl + url // Usa el motor seleccionado si no es una URL directa
        } else {
            url
        }
        webView.loadUrl(urlFinal)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }

    // --- ADAPTADOR INTERNO PARA EL MENÚ DE MOTORES ---
    inner class MotorAdapter(context: Context, private val motores: List<MotorBusqueda>) :
        ArrayAdapter<MotorBusqueda>(context, 0, motores) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return crearFila(position, convertView, parent)
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            return crearFila(position, convertView, parent)
        }

        private fun crearFila(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_motor_busqueda, parent, false)
            val motor = getItem(position)

            val icono = view.findViewById<ImageView>(R.id.ivMotorIcono)
            val nombre = view.findViewById<TextView>(R.id.tvMotorNombre)

            motor?.let {
                icono.setImageResource(it.icono)
                nombre.text = it.nombre
            }
            return view
        }
    }
}
