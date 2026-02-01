package com.tuapp.calculadora

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.view.accessibility.AccessibilityEvent

class AppLockService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // 
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageNameAbierta = event.packageName?.toString() ?: return

            // 
            val prefs = getSharedPreferences("AppsBloqueadasPrefs", Context.MODE_PRIVATE)
            val appsBloqueadas = prefs.getStringSet("lista_bloqueadas", setOf()) ?: setOf()

            // 
            if (appsBloqueadas.contains(packageNameAbierta)) {
                
                // 
                if (packageNameAbierta != packageName) {
                    lanzarPantallaDeBloqueo()
                }
            }
        }
    }

    private fun lanzarPantallaDeBloqueo() {
        val intent = Intent(this, LockActivity::class.java)
        // 
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    override fun onInterrupt() {
        // 
    }
}
