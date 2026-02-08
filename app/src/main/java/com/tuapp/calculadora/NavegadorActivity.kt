package com.triskanv.calculadora // Asegúrate de que este sea tu package real

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
import android.webkit.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

data class MotorBusqueda(val nombre: String, val url: String, val icono: Int)

class NavegadorActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private var motorActualUrl = "https://www.google.com/search?q="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // SEGURIDAD: Bloquear capturas
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

        // --- CONFIGURACIÓN DEL WEBVIEW ---
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            allowFileAccess = true
            javaScriptCanOpenWindowsAutomatically = true
            mediaPlaybackRequiresUserGesture = false
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            userAgentString = "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Mobile Safari/537.36"
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                return false 
            }
        }

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

        if (savedInstanceState == null) {
            webView.loadUrl("https://www.google.com")
        }
    }

    /**
     * COMENTADO: Evita que la app se cierre al saltar diálogos de sistema o permisos.
     */
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        // finish() // Comentado para debugging de crashes
    }

    private fun cargarUrl(url: String) {
        val urlFinal = if (!url.startsWith("http://") && !url.startsWith("https://")) {
            if (url.contains(".") && !url.contains(" ")) "https://$url" else motorActualUrl + url
        } else {
            url
        }
        webView.loadUrl(urlFinal)
    }

    private fun gestionarDescarga(url: String, userAgent: String, contentDisposition: String?, mimetype: String?) {
        try {
            // Refinar MimeType para evitar .bin
            var mimeEfectivo = mimetype
            if (url.contains(".jpg", true) || url.contains(".jpeg", true)) mimeEfectivo = "image/jpeg"
            if (url.contains(".png", true)) mimeEfectivo = "image/png"
            if (url.contains(".mp4", true)) mimeEfectivo = "video/mp4"

            var fileName = URLUtil.guessFileName(url, contentDisposition, mimeEfectivo)

            if (fileName.endsWith(".bin") || !fileName.contains(".")) {
                val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeEfectivo)
                fileName = "file_${System.currentTimeMillis()}.${extension ?: "jpg"}"
            }

            // Clasificación
            val extensionUrl = MimeTypeMap.getFileExtensionFromUrl(url).lowercase()
            val subCarpeta = when {
                mimeEfectivo?.startsWith("image/") == true || listOf("jpg", "jpeg", "png", "webp").contains(extensionUrl) -> "FotosSecretas"
                mimeEfectivo?.startsWith("video/") == true || listOf("mp4", "mkv", "mov", "avi").contains(extensionUrl) -> "VideosSecretos"
                else -> Environment.DIRECTORY_DOWNLOADS
            }

            val request = DownloadManager.Request(Uri.parse(url)).apply {
                setMimeType(mimeEfectivo)
                addRequestHeader("cookie", CookieManager.getInstance().getCookie(url))
                addRequestHeader("User-Agent", userAgent)
                setTitle(fileName)
                setDescription("Guardando en $subCarpeta")
                setDestinationInExternalFilesDir(this@NavegadorActivity, subCarpeta, fileName)
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            }

            val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
            Toast.makeText(this, "Descarga iniciada", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val result = webView.hitTestResult
        if (result.type == WebView.HitTestResult.IMAGE_TYPE || result.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
            menu.setHeaderTitle("Imagen Privada")
            menu.add(0, 1, 0, "Guardar en Bóveda").setOnMenuItemClickListener {
                result.extra?.let { gestionarDescarga(it, webView.settings.userAgentString, null, null) }
                true
            }
        }
    }

    override fun onDestroy() {
        webView.apply {
            stopLoading()
            clearCache(true)
            clearHistory()
            removeAllViews()
            destroy()
        }
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack() else finish()
    }

    inner class MotorAdapter(context: Context, private val motores: List<MotorBusqueda>) :
        ArrayAdapter<MotorBusqueda>(context, 0, motores) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View = crearFila(position, convertView, parent)
        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View = crearFila(position, convertView, parent)
        private fun crearFila(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_motor_busqueda, parent, false)
            val motor = getItem(position)
            view.findViewById<ImageView>(R.id.ivMotorIcono).setImageResource(motor?.icono ?: 0)
            view.findViewById<TextView>(R.id.tvMotorNombre).text = motor?.nombre
            return view
        }
    }
}
