package com.tuapp.calculadora

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
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

        // Inicializar vistas
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

        // 2. Adaptador para el Spinner (Menú de motores)
        val adapter = MotorAdapter(this, listaMotores)
        spinnerMotores.adapter = adapter

        spinnerMotores.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                motorActualUrl = listaMotores[position].url
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // 3. Configuración del WebView
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.webViewClient = WebViewClient()

        // Habilitar menú contextual para descargar imágenes/videos
        registerForContextMenu(webView)

        // Escuchador de descargas (para APKs, ZIPs, etc.)
        webView.setDownloadListener { url, userAgent, contentDisposition, mimetype, _ ->
            gestionarDescarga(url, userAgent, contentDisposition, mimetype)
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                progressBar.progress = newProgress
                progressBar.visibility = if (newProgress < 100) View.VISIBLE else View.GONE
            }
        }

        // Acciones de búsqueda
        btnIr.setOnClickListener { cargarUrl(etUrl.text.toString()) }
        etUrl.setOnEditorActionListener { _, _, _ ->
            cargarUrl(etUrl.text.toString())
            true
        }

        // Página de inicio por defecto
        webView.loadUrl("https://www.google.com")
    }

    private fun cargarUrl(url: String) {
        val urlFinal = if (!url.startsWith("http://") && !url.startsWith("https://")) {
            motorActualUrl + url
        } else {
            url
        }
        webView.loadUrl(urlFinal)
    }

    // 4. Gestión de descargas al sistema
    private fun gestionarDescarga(url: String, userAgent: String, contentDisposition: String?, mimetype: String?) {
        try {
            val request = DownloadManager.Request(Uri.parse(url))
            val fileName = URLUtil.guessFileName(url, contentDisposition, mimetype)

            request.setMimeType(mimetype)
            val cookies = android.webkit.CookieManager.getInstance().getCookie(url)
            request.addRequestHeader("cookie", cookies)
            request.addRequestHeader("User-Agent", userAgent)

            request.setTitle(fileName)
            request.setDescription("Descargando desde Navegador Secreto")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

            val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)

            Toast.makeText(this, "Iniciando descarga: $fileName", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al descargar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // 5. Menú para descargar al mantener presionado (Corregido para evitar errores de compilación)
    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val result = webView.hitTestResult
        val tipo = result.type

        // 5 = IMAGE_TYPE, 8 = SRC_IMAGE_URI_TYPE, 9 = SRC_VIDEO_TYPE
        if (tipo == 5 || tipo == 8 || tipo == 9) {
            menu.setHeaderTitle("Acción de Archivo")
            menu.add(0, 1, 0, "Guardar en Descargas").setOnMenuItemClickListener {
                val url = result.extra
                if (url != null) {
                    gestionarDescarga(url, webView.settings.userAgentString, null, null)
                }
                true
            }
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }

    // Adaptador del Menú desplegable
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
