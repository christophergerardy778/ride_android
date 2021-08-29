package com.example.inride.ui

import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import com.example.inride.ui.LoginActivity
import com.example.inride.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.materialButton.setOnClickListener { startHomeActivity() }
        viewBinding.mbSignUp.setOnClickListener{ startActivity(Intent(this, SignUpActivity::class.java)) }
    }

    private fun startHomeActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}