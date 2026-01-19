private fun moverFotoABoveda(uriOriginal: Uri) {
    val inputStream = contentResolver.openInputStream(uriOriginal)
    val nombreArchivo = "IMG_${System.currentTimeMillis()}.jpg"
    val archivoDestino = File(getExternalFilesDir(null), "MisFotosSecretas/$nombreArchivo")
    
    // Crear carpeta si no existe
    archivoDestino.parentFile?.mkdirs()

    // Copiar el archivo a la boveda
    archivoDestino.outputStream().use { output ->
        inputStream?.copyTo(output)
    }

    // ELIMINAR de la galería pública 
    contentResolver.delete(uriOriginal, null, null)
    
    Toast.makeText(this, "Foto protegida y eliminada de galería", Toast.LENGTH_SHORT).show()
    cargarFotos() // 
}
