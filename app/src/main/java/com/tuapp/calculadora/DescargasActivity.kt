package com.tuapp.calculadora

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class DescargasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        
        supportActionBar?.hide()

        
        setContentView(R.layout.activity_descargas)
    }
}
