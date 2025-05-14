package com.example.mpi.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mpi.R
import com.example.mpi.databinding.ActivityMenuBinding
import com.example.mpi.ui.acao.AcaoActivity
import com.example.mpi.ui.atividade.AtividadeActivity
import com.example.mpi.ui.dashboard.DashboardActivity
import com.example.mpi.ui.pilar.PilarActivity
import com.example.mpi.ui.subpilar.SubpilarActivity

class MenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val intent = getIntent()
        val tipo = intent.getStringExtra("tipo") ?: "valor padrão"
        Log.d("danielgois_project", tipo)

        Toast.makeText(this, tipo, Toast.LENGTH_LONG)

        val openPilar: Button = findViewById(R.id.btnPilarActivity)
        openPilar.setOnClickListener {
            startActivity(Intent(this, PilarActivity::class.java))
        }

        val openSubpilar: Button = findViewById(R.id.btnSubpilarActivity)
        openSubpilar.setOnClickListener {
            startActivity(Intent(this, SubpilarActivity::class.java))
        }

        val openAcao: Button = findViewById(R.id.btnAcaoActivity)
        openAcao.setOnClickListener {
            startActivity(Intent(this, AcaoActivity::class.java))
        }

        val openAtividade: Button = findViewById(R.id.btnAtividadeActivity)
        openAtividade.setOnClickListener {
            startActivity(Intent(this, AtividadeActivity::class.java))
        }

        val openAprovacao: Button = findViewById(R.id.btnAprovacaoActivity)
        openAprovacao.setOnClickListener {
            startActivity(Intent(this, AprovacaoActivity::class.java))
        }

        val openFinalizacao: Button = findViewById(R.id.btnFinalizacaoActivity)
        openFinalizacao.setOnClickListener {
            startActivity(Intent(this, FinalizacaoActivity::class.java))
        }

        val openPercentual: Button = findViewById(R.id.btnPercentualActivity)
        openPercentual.setOnClickListener {
            startActivity(Intent(this, PercentualActivity::class.java))
        }

        val openDashboard: Button = findViewById(R.id.btnDashboardActivity)
        openDashboard.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }

        val openNotificacao: ImageView = findViewById(R.id.btnNotificacaoActivity)
        openNotificacao.setOnClickListener {
            startActivity(Intent(this, NotificacaoActivity::class.java))
        }
    }
}