package com.example.mpi.ui.subpilar

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mpi.data.Subpilar
import com.example.mpi.data.SubpilarDbHelper
import com.example.mpi.databinding.ActivityEditarSubpilarBinding

class EditarSubpilarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarSubpilarBinding
    private lateinit var dbHelper: SubpilarDbHelper
    private var subpilarId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarSubpilarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = SubpilarDbHelper(this)

        // Recupera os dados do subpilar da Intent
        val extras = intent.extras
        if (extras != null) {
            subpilarId = extras.getLong("subpilar_id", -1)
            val nome = extras.getString("subpilar_nome")
            val descricao = extras.getString("subpilar_descricao")
            val dataInicio = extras.getString("subpilar_data_inicio")
            val dataTermino = extras.getString("subpilar_data_termino")
            val aprovado = extras.getBoolean("subpilar_aprovado")
            val percentual = extras.getDouble("subpilar_percentual")

            // Preenche os campos de edição
            binding.etEditarNomeSubpilar.setText(nome)
            binding.etEditarDescricaoSubpilar.setText(descricao)
            binding.etEditarDataInicio.setText(dataInicio)
            binding.etEditarDataTermino.setText(dataTermino)
            binding.tvExibirAprovado.text = if (aprovado) "Sim" else "Não"
        }

        binding.btnSalvarEdicao.setOnClickListener {
            salvarEdicaoSubpilar()
        }

        binding.btnVoltarEditar.setOnClickListener {
            finish() // Simplesmente finaliza a EditarSubpilarActivity e volta para SubpilarActivity
        }
    }

    private fun salvarEdicaoSubpilar() {
        val nome = binding.etEditarNomeSubpilar.text.toString()
        val descricao = binding.etEditarDescricaoSubpilar.text.toString()
        val dataInicio = binding.etEditarDataInicio.text.toString()
        val dataTermino = binding.etEditarDataTermino.text.toString()

        if (nome.isEmpty() || descricao.isEmpty() || dataInicio.isEmpty() || dataTermino.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            return
        }


        if (subpilarId != -1L) {
            val db = dbHelper.writableDatabase
            val values = android.content.ContentValues().apply {
                put(com.example.mpi.data.SubpilarContract.UserEntry.COL_NOME, nome)
                put(com.example.mpi.data.SubpilarContract.UserEntry.COL_DESCRICAO, descricao)
                put(com.example.mpi.data.SubpilarContract.UserEntry.COL_DATAI, dataInicio)
                put(com.example.mpi.data.SubpilarContract.UserEntry.COL_DATAT, dataTermino)
            }

            val whereClause = "${android.provider.BaseColumns._ID} = ?"
            val whereArgs = arrayOf(subpilarId.toString())
            val rowsAffected = db.update(
                com.example.mpi.data.SubpilarContract.UserEntry.TABLE_NAME,
                values,
                whereClause,
                whereArgs
            )

            if (rowsAffected > 0) {
                Toast.makeText(this, "Subpilar atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Erro ao atualizar o subpilar.", Toast.LENGTH_SHORT).show()
            }
            db.close()
        } else {
            Toast.makeText(this, "Erro: ID do subpilar não encontrado.", Toast.LENGTH_SHORT).show()
        }
    }
}