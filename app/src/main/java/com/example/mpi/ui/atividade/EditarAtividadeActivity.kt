package com.example.mpi.ui.atividade

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mpi.data.Atividade
import com.example.mpi.data.AtividadeDbHelper
import com.example.mpi.databinding.ActivityEditarAtividadeBinding

class EditarAtividadeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarAtividadeBinding
    private lateinit var dbHelper: AtividadeDbHelper
    private var atividadeId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarAtividadeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = AtividadeDbHelper(this)

        val extras = intent.extras
        if (extras != null) {
            atividadeId = extras.getLong("atividade_id", -1)
            val nome = extras.getString("atividade_nome")
            val descricao = extras.getString("atividade_descricao")
            val dataInicio = extras.getString("atividade_data_inicio")
            val dataTermino = extras.getString("atividade_data_termino")
            val codigoResponsavel = extras.getString("atividade_codigo_responsavel")
            val aprovado = extras.getBoolean("atividade_aprovado")
            val finalizada = extras.getBoolean("atividade_finalizada")
            val orcamento = extras.getDouble("atividade_orcamento", 0.0)

            binding.etEditarNomeAtividade.setText(nome)
            binding.etEditarDescricaoAtividade.setText(descricao)
            binding.etEditarDataInicio.setText(dataInicio)
            binding.etEditarDataTermino.setText(dataTermino)
            binding.etEditarCodigoResponsavel.setText(codigoResponsavel)
            binding.tvExibirAprovado.text = if (aprovado) "Sim" else "Não"
            binding.tvExibirFinalizada.text = if (finalizada) "Sim" else "Não"
            binding.etEditarOrcamentoAtividade.setText(if (orcamento != 0.0) String.format("%.2f", orcamento) else "")
        }

        binding.btnSalvarEdicao.setOnClickListener {
            salvarEdicaoAtividade()
        }

        binding.btnVoltarEditar.setOnClickListener {
            finish()
        }
    }

    private fun salvarEdicaoAtividade() {
        val nome = binding.etEditarNomeAtividade.text.toString()
        val descricao = binding.etEditarDescricaoAtividade.text.toString()
        val dataInicio = binding.etEditarDataInicio.text.toString()
        val dataTermino = binding.etEditarDataTermino.text.toString()
        val codigoResponsavel = binding.etEditarCodigoResponsavel.text.toString()
        val orcamentoStr = binding.etEditarOrcamentoAtividade.text.toString()
        val orcamento = if (orcamentoStr.isNotEmpty()) orcamentoStr.toDouble() else 0.0

        if (nome.isEmpty() || descricao.isEmpty() || dataInicio.isEmpty() || dataTermino.isEmpty() || codigoResponsavel.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show()
            return
        }

        if (atividadeId != -1L) {
            val db = dbHelper.writableDatabase
            val values = android.content.ContentValues().apply {
                put(com.example.mpi.data.AtividadeContract.UserEntry.COL_NOME, nome)
                put(com.example.mpi.data.AtividadeContract.UserEntry.COL_DESCRICAO, descricao)
                put(com.example.mpi.data.AtividadeContract.UserEntry.COL_DATAI, dataInicio)
                put(com.example.mpi.data.AtividadeContract.UserEntry.COL_DATAT, dataTermino)
                put(com.example.mpi.data.AtividadeContract.UserEntry.COL_RESPONSAVEL, codigoResponsavel)
                put(com.example.mpi.data.AtividadeContract.UserEntry.COL_ORCAMENTO, orcamento)
            }

            val whereClause = "${android.provider.BaseColumns._ID} = ?"
            val whereArgs = arrayOf(atividadeId.toString())
            val rowsAffected = db.update(
                com.example.mpi.data.AtividadeContract.UserEntry.TABLE_NAME,
                values,
                whereClause,
                whereArgs
            )

            if (rowsAffected > 0) {
                Toast.makeText(this, "Atividade atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Erro ao atualizar a atividade.", Toast.LENGTH_SHORT).show()
            }
            db.close()
        } else {
            Toast.makeText(this, "Erro: ID da atividade não encontrado.", Toast.LENGTH_SHORT).show()
        }
    }
}