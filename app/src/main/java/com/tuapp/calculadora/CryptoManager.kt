package com.tuapp.calculadora

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.*
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class CryptoManager {

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    
    private fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry("secret_key", null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

    private fun createKey(): SecretKey {
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore").apply {
            init(KeyGenParameterSpec.Builder("secret_key", 
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build())
        }.generateKey()
    }

    // Usado para CIFRAR tanto Fotos como Videos
    fun encrypt(inputStream: InputStream, outputStream: OutputStream) {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getKey())
        
        // Guardamos el IV al inicio para poder descifrar después
        outputStream.write(cipher.iv.size)
        outputStream.write(cipher.iv)

        val buffer = ByteArray(8192)
        var bytesRead: Int
        val cipherOutputStream = CipherOutputStream(outputStream, cipher)
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            cipherOutputStream.write(buffer, 0, bytesRead)
        }
        cipherOutputStream.close()
        inputStream.close()
    }

    // Usado para FOTOS (descifra directo a memoria RAM)
    fun decrypt(inputStream: InputStream): ByteArray {
        val ivSize = inputStream.read()
        val iv = ByteArray(ivSize)
        inputStream.read(iv)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, getKey(), GCMParameterSpec(128, iv))
        
        val decryptedBytes = cipher.doFinal(inputStream.readBytes())
        inputStream.close()
        return decryptedBytes
    }

    // NUEVO: Usado para VIDEOS (descifra de archivo a archivo temporal sin explotar la RAM)
    fun decryptToStream(inputStream: InputStream, outputStream: OutputStream) {
        val ivSize = inputStream.read()
        if (ivSize == -1) return
        
        val iv = ByteArray(ivSize)
        inputStream.read(iv)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, getKey(), GCMParameterSpec(128, iv))

        val cipherInputStream = CipherInputStream(inputStream, cipher)
        val buffer = ByteArray(8192)
        var bytesRead: Int
        
        try {
            while (cipherInputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
        } finally {
            cipherInputStream.close()
            outputStream.close()
            inputStream.close()
        }
    }
}
