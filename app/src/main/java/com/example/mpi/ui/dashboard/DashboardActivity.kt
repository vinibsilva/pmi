package com.example.mpi.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mpi.R
import com.example.mpi.databinding.ActivityDashboardBinding
import com.example.mpi.ui.MenuActivity

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDashboardBinding.inflate(layoutInflater)
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

        val openOpcao1: Button = findViewById(R.id.btnOption1)
        openOpcao1.setOnClickListener {
            startActivity(Intent(this, Opcao1Activity::class.java))
        }
    }
}