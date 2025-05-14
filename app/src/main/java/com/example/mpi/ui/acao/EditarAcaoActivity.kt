package com.example.mpi.ui.acao

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mpi.data.Acao
import com.example.mpi.data.AcaoDbHelper
import com.example.mpi.databinding.ActivityEditarAcaoBinding

class EditarAcaoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarAcaoBinding
    private lateinit var dbHelper: AcaoDbHelper
    private var acaoId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarAcaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = AcaoDbHelper(this)

        // Recupera os dados da ação da Intent
        val extras = intent.extras
        if (extras != null) {
            acaoId = extras.getLong("acao_id", -1)
            val nome = extras.getString("acao_nome")
            val descricao = extras.getString("acao_descricao")
            val dataInicio = extras.getString("acao_data_inicio")
            val dataTermino = extras.getString("acao_data_termino")
            val codigoResponsavel = extras.getString("acao_codigo_responsavel")
            val aprovado = extras.getBoolean("acao_aprovado")
            val finalizada = extras.getBoolean("acao_finalizada")

            // Preenche os campos de edição
            binding.etEditarNomeAcao.setText(nome)
            binding.etEditarDescricaoAcao.setText(descricao)
            binding.etEditarDataInicio.setText(dataInicio)
            binding.etEditarDataTermino.setText(dataTermino)
            binding.etEditarCodigoResponsavel.setText(codigoResponsavel)
            binding.tvExibirAprovado.text = if (aprovado) "Sim" else "Não"
            binding.tvExibirFinalizada.text = if (finalizada) "Sim" else "Não"
        }

        binding.btnSalvarEdicao.setOnClickListener {
            salvarEdicaoAcao()
        }

        binding.btnVoltarEditar.setOnClickListener {
            finish() // Simplesmente finaliza a EditarAcaoActivity e volta para AcaoActivity
        }
    }

    private fun salvarEdicaoAcao() {
        val nome = binding.etEditarNomeAcao.text.toString()
        val descricao = binding.etEditarDescricaoAcao.text.toString()
        val dataInicio = binding.etEditarDataInicio.text.toString()
        val dataTermino = binding.etEditarDataTermino.text.toString()
        val codigoResponsavel = binding.etEditarCodigoResponsavel.text.toString()

        if (nome.isEmpty() || descricao.isEmpty() || dataInicio.isEmpty() || dataTermino.isEmpty() || codigoResponsavel.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            return
        }


        if (acaoId != -1L) {
            val db = dbHelper.writableDatabase
            val values = android.content.ContentValues().apply {
                put(com.example.mpi.data.AcaoContract.UserEntry.COL_NOME, nome)
                put(com.example.mpi.data.AcaoContract.UserEntry.COL_DESCRICAO, descricao)
                put(com.example.mpi.data.AcaoContract.UserEntry.COL_DATAI, dataInicio)
                put(com.example.mpi.data.AcaoContract.UserEntry.COL_DATAT, dataTermino)
                put(com.example.mpi.data.AcaoContract.UserEntry.COL_RESPONSAVEL, codigoResponsavel)
            }

            val whereClause = "${android.provider.BaseColumns._ID} = ?"
            val whereArgs = arrayOf(acaoId.toString())
            val rowsAffected = db.update(
                com.example.mpi.data.AcaoContract.UserEntry.TABLE_NAME,
                values,
                whereClause,
                whereArgs
            )

            if (rowsAffected > 0) {
                Toast.makeText(this, "Ação atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Erro ao atualizar a ação.", Toast.LENGTH_SHORT).show()
            }
            db.close()
        } else {
            Toast.makeText(this, "Erro: ID da ação não encontrado.", Toast.LENGTH_SHORT).show()
        }
    }
}