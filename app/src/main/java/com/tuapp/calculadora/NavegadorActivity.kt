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
import android.view.WindowManager
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.CookieManager
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.File

data class MotorBusqueda(val nombre: String, val url: String, val icono: Int)

class NavegadorActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private var motorActualUrl = "https://www.google.com/search?q="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        setContentView(R.layout.activity_navegador)

        webView = findViewById(R.id.webView)
        progressBar = findViewById(R.id.pbCarga)
        val etUrl = findViewById<EditText>(R.id.etUrl)
        val btnIr = findViewById<ImageButton>(R.id.btnIr)
        val spinnerMotores = findViewById<Spinner>(R.id.spinnerMotores)

        val listaMotores = listOf(
            MotorBusqueda("Google", "https://www.google.com/search?q=", android.R.drawable.ic_menu_search),
            MotorBusqueda("DuckDuckGo", "https://duckduckgo.com/?q=", android.R.drawable.ic_menu_view),
            MotorBusqueda("Mojeek", "https://www.mojeek.com/search?q=", android.R.drawable.ic_menu_compass),
            MotorBusqueda("Searx", "https://searx.be/search?q=", android.R.drawable.ic_menu_manage)
        )

        val adapter = MotorAdapter(this, listaMotores)
        spinnerMotores.adapter = adapter

        spinnerMotores.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                motorActualUrl = listaMotores[position].url
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.databaseEnabled = false 
        webView.webViewClient = WebViewClient()

        registerForContextMenu(webView)

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

        btnIr.setOnClickListener { cargarUrl(etUrl.text.toString()) }
        etUrl.setOnEditorActionListener { _, _, _ ->
            cargarUrl(etUrl.text.toString())
            true
        }

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

    private fun gestionarDescarga(url: String, userAgent: String, contentDisposition: String?, mimetype: String?) {
        try {
            val request = DownloadManager.Request(Uri.parse(url))
            
            // --- MEJORA: DETECTOR DE EXTENSIÓN REAL ---
            var fileName = URLUtil.guessFileName(url, contentDisposition, mimetype)
            
            // Si el nombre termina en .bin, intentamos corregirlo con el mimetype
            if (fileName.endsWith(".bin") && mimetype != null) {
                val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimetype)
                if (extension != null) {
                    fileName = fileName.substringBeforeLast(".") + "." + extension
                }
            }

            request.setMimeType(mimetype)
            val cookies = CookieManager.getInstance().getCookie(url)
            request.addRequestHeader("cookie", cookies)
            request.addRequestHeader("User-Agent", userAgent)

            request.setTitle(fileName)
            request.setDescription("Protegiendo archivo en la boveda...")
            
            request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, fileName)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)

            Toast.makeText(this, "Descarga iniciada: $fileName", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al descargar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val result = webView.hitTestResult
        val tipo = result.type

        if (tipo == 5 || tipo == 8 || tipo == 9) {
            menu.setHeaderTitle("Acción de Archivo")
            menu.add(0, 1, 0, "Guardar de forma privada").setOnMenuItemClickListener {
                val url = result.extra
                if (url != null) {
                    gestionarDescarga(url, webView.settings.userAgentString, null, null)
                }
                true
            }
        }
    }

    override fun onDestroy() {
        webView.clearCache(true)
        webView.clearHistory()
        webView.clearFormData()
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            finish() 
        }
    }

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
