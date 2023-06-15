package com.example.cari_matang2.presentation.landing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.cari_matang2.databinding.ActivityLandingBinding
import com.example.cari_matang2.presentation.login.LoginActivity

class LandingActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLandingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)


        onAction()
    }

    private fun onAction() {
        binding.btnLanding.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }


}