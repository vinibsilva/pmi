package com.example.mpi.ui.pilar

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mpi.data.Pilar
import com.example.mpi.data.PilarDbHelper
import com.example.mpi.databinding.ActivityEditarPilarBinding

class EditarPilarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarPilarBinding
    private lateinit var dbHelper: PilarDbHelper
    private var pilarId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarPilarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = PilarDbHelper(this)

        // Recupera os dados do pilar da Intent
        val extras = intent.extras
        if (extras != null) {
            pilarId = extras.getLong("pilar_id", -1)
            val nome = extras.getString("pilar_nome")
            val descricao = extras.getString("pilar_descricao")
            val dataInicio = extras.getString("pilar_data_inicio")
            val dataTermino = extras.getString("pilar_data_termino")
            val aprovado = extras.getBoolean("pilar_aprovado")
            val percentual = extras.getDouble("pilar_percentual")

            // Preenche os campos de edição
            binding.etEditarNomePilar.setText(nome)
            binding.etEditarDescricaoPilar.setText(descricao)
            binding.etEditarDataInicio.setText(dataInicio)
            binding.etEditarDataTermino.setText(dataTermino)
            binding.tvExibirPercentual.text = String.format("%.2f%%", percentual * 100)
            binding.tvExibirAprovado.text = if (aprovado) "Sim" else "Não"
        }

        binding.btnSalvarEdicao.setOnClickListener {
            salvarEdicaoPilar()
        }

        binding.btnVoltarEditar.setOnClickListener {
            finish() // Simplesmente finaliza a EditarPilarActivity e volta para PilarActivity
        }
    }

    private fun salvarEdicaoPilar() {
        val nome = binding.etEditarNomePilar.text.toString()
        val descricao = binding.etEditarDescricaoPilar.text.toString()
        val dataInicio = binding.etEditarDataInicio.text.toString()
        val dataTermino = binding.etEditarDataTermino.text.toString()

        if (nome.isEmpty() || descricao.isEmpty() || dataInicio.isEmpty() || dataTermino.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            return
        }


        if (pilarId != -1L) {
            val db = dbHelper.writableDatabase
            val values = android.content.ContentValues().apply {
                put(com.example.mpi.data.PilarContract.UserEntry.COL_NOME, nome)
                put(com.example.mpi.data.PilarContract.UserEntry.COL_DESCRICAO, descricao)
                put(com.example.mpi.data.PilarContract.UserEntry.COL_DATAI, dataInicio)
                put(com.example.mpi.data.PilarContract.UserEntry.COL_DATAT, dataTermino)
                // Os campos de percentual e aprovado NÃO são atualizados aqui
            }

            val whereClause = "${android.provider.BaseColumns._ID} = ?"
            val whereArgs = arrayOf(pilarId.toString())
            val rowsAffected = db.update(
                com.example.mpi.data.PilarContract.UserEntry.TABLE_NAME,
                values,
                whereClause,
                whereArgs
            )

            if (rowsAffected > 0) {
                Toast.makeText(this, "Pilar atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish() // Volta para PilarActivity após a atualização
            } else {
                Toast.makeText(this, "Erro ao atualizar o pilar.", Toast.LENGTH_SHORT).show()
            }
            db.close()
        } else {
            Toast.makeText(this, "Erro: ID do pilar não encontrado.", Toast.LENGTH_SHORT).show()
        }
    }
}