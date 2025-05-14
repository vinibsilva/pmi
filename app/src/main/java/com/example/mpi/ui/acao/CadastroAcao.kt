package com.example.mpi.ui.acao

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mpi.R
import com.example.mpi.databinding.ActivityCadastroAcaoBinding
import com.example.mpi.data.AcaoDbHelper
import com.example.mpi.data.AcaoContract


class CadastroAcaoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroAcaoBinding
    private lateinit var dbHelper: AcaoDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCadastroAcaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = AcaoDbHelper(this)

        binding.confirmarCadastro.setOnClickListener{
            val nome = binding.nomeAcao.text.toString()
            val descricao = binding.descricaoAcao.text.toString()
            val dataInicio = binding.dataInicio.text.toString().trim()
            val dataTermino = binding.dataTermino.text.toString().trim()
            val codigoResponsavel = binding.codigoResponsavel.text.toString().trim()

            if (nome.isEmpty() || descricao.isEmpty() || dataInicio.isEmpty() || dataTermino.isEmpty() || codigoResponsavel.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //Convertendo o valor de codigo de responsável para Integer, ele estava como String para que o métod isemmpety fosse acessível
            val codigoResponsavelInt = codigoResponsavel.toIntOrNull()

            val db = dbHelper.writableDatabase
            val values = android.content.ContentValues().apply {
                put(AcaoContract.UserEntry.COL_NOME, nome)
                put(AcaoContract.UserEntry.COL_DESCRICAO, descricao)
                put(AcaoContract.UserEntry.COL_DATAI, dataInicio)
                put(AcaoContract.UserEntry.COL_DATAT, dataTermino)
                put(AcaoContract.UserEntry.COL_RESPONSAVEL, codigoResponsavel)
            }

            val newRowId = db.insert(AcaoContract.UserEntry.TABLE_NAME, null, values)
            if (newRowId == -1L) {
                Toast.makeText(this, "Erro ao cadastrar — A Ação já existe?", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Ação cadastrada com sucesso! Atualize a página de listagem.", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }
        }

        binding.cancelarCadastro.setOnClickListener{
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}