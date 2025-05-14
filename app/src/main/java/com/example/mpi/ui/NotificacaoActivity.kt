package com.example.mpi.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mpi.R
import com.example.mpi.databinding.ActivityNotificacaoBinding
import com.example.mpi.databinding.ActivityPercentualBinding

class NotificacaoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificacaoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityNotificacaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val openMenuPrincipal: ImageView = findViewById(R.id.viewVoltarMenuPrincipal)
        openMenuPrincipal.setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
        }

    }
}