package com.example.mpi.ui.atividade

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mpi.R
import com.example.mpi.databinding.ActivityCadastroAtividadeBinding
import com.example.mpi.data.AtividadeDbHelper
import com.example.mpi.data.AtividadeContract


class CadastroAtividadeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroAtividadeBinding
    private lateinit var dbHelper: AtividadeDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCadastroAtividadeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = AtividadeDbHelper(this)

        binding.btnConfirmarCadastroAtividade.setOnClickListener{
            val nome = binding.etNomeAtividade.text.toString()
            val descricao = binding.etDescricaoAtividade.text.toString()
            val dataInicio = binding.etDataInicioAtividade.text.toString().trim()
            val dataTermino = binding.etDataTerminoAtividade.text.toString().trim()
            val codigoResponsavel = binding.etCodigoResponsavelAtividade.text.toString().trim()
            val orcamento = binding.etOrcamentoAtividade.text.toString().trim()

            if (nome.isEmpty() || descricao.isEmpty() || dataInicio.isEmpty() || dataTermino.isEmpty() || codigoResponsavel.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val codigoResponsavelInt = codigoResponsavel.toIntOrNull()

            val db = dbHelper.writableDatabase
            val values = android.content.ContentValues().apply {
                put(AtividadeContract.UserEntry.COL_NOME, nome)
                put(AtividadeContract.UserEntry.COL_DESCRICAO, descricao)
                put(AtividadeContract.UserEntry.COL_DATAI, dataInicio)
                put(AtividadeContract.UserEntry.COL_DATAT, dataTermino)
                put(AtividadeContract.UserEntry.COL_ORCAMENTO, orcamento )
                put(AtividadeContract.UserEntry.COL_RESPONSAVEL, codigoResponsavel)
            }

            val newRowId = db.insert(AtividadeContract.UserEntry.TABLE_NAME, null, values)
            if (newRowId == -1L) {
                Toast.makeText(this, "Erro ao cadastrar — A Atividade já existe?", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Atividade cadastrada com sucesso! Atualize a página de listagem.", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }
        }

        binding.btnVoltarAtividade.setOnClickListener{
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}