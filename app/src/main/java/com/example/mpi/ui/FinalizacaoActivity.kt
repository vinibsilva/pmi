package com.example.mpi.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mpi.databinding.ActivityFinalizacaoBinding
import com.example.mpi.ui.MenuActivity

class FinalizacaoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinalizacaoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinalizacaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVoltarInicio.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
