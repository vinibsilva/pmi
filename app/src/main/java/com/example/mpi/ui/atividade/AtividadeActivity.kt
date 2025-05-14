package com.example.mpi.ui.atividade

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mpi.data.Atividade
import com.example.mpi.data.AtividadeDbHelper
import com.example.mpi.databinding.ActivityAtividadeBinding
import com.example.mpi.ui.atividade.AtividadeAdapter
import com.example.mpi.ui.atividade.CadastroAtividadeActivity
import com.example.mpi.ui.atividade.EditarAtividadeActivity


class AtividadeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAtividadeBinding
    private lateinit var dbHelper: AtividadeDbHelper
    private lateinit var atividadeAdapter: AtividadeAdapter
    private val listaAtividades = mutableListOf<Atividade>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAtividadeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = AtividadeDbHelper(this)


        binding.recyclerViewAtividades.layoutManager = LinearLayoutManager(this)
        atividadeAdapter = AtividadeAdapter(listaAtividades, { atividade -> editarAtividade(atividade) }, { atividade -> excluirAtividade(atividade) })
        binding.recyclerViewAtividades.adapter = atividadeAdapter

        carregarAtividades()

        binding.btnCadastrarAtividade.setOnClickListener {
            android.widget.Toast.makeText(this, "Adicionar nova atividade", android.widget.Toast.LENGTH_SHORT).show()
            val intent = android.content.Intent(this, CadastroAtividadeActivity::class.java)
            startActivity(intent)
        }
        binding.btnVoltar.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        carregarAtividades()
    }

    private fun carregarAtividades() {
        listaAtividades.clear()
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            android.provider.BaseColumns._ID,
            com.example.mpi.data.AtividadeContract.UserEntry.COL_NOME,
            com.example.mpi.data.AtividadeContract.UserEntry.COL_DESCRICAO,
            com.example.mpi.data.AtividadeContract.UserEntry.COL_DATAI,
            com.example.mpi.data.AtividadeContract.UserEntry.COL_DATAT,
            com.example.mpi.data.AtividadeContract.UserEntry.COL_RESPONSAVEL,
            com.example.mpi.data.AtividadeContract.UserEntry.COL_APROVADO,
            com.example.mpi.data.AtividadeContract.UserEntry.COL_FINALIZADO,
            com.example.mpi.data.AtividadeContract.UserEntry.COL_ORCAMENTO
        )

        val cursor = db.query(
            com.example.mpi.data.AtividadeContract.UserEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )


        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(android.provider.BaseColumns._ID))
                val nome = getString(getColumnIndexOrThrow(com.example.mpi.data.AtividadeContract.UserEntry.COL_NOME))
                val descricao = getString(getColumnIndexOrThrow(com.example.mpi.data.AtividadeContract.UserEntry.COL_DESCRICAO))
                val dataInicio = getString(getColumnIndexOrThrow(com.example.mpi.data.AtividadeContract.UserEntry.COL_DATAI))
                val dataTermino = getString(getColumnIndexOrThrow(com.example.mpi.data.AtividadeContract.UserEntry.COL_DATAT))
                val codigoResponsavel = getInt(getColumnIndexOrThrow(com.example.mpi.data.AtividadeContract.UserEntry.COL_RESPONSAVEL))
                val aprovado = getInt(getColumnIndexOrThrow(com.example.mpi.data.AtividadeContract.UserEntry.COL_APROVADO))
                val finalizada = getInt(getColumnIndexOrThrow(com.example.mpi.data.AtividadeContract.UserEntry.COL_FINALIZADO))
                val orcamento = getDouble(getColumnIndexOrThrow(com.example.mpi.data.AtividadeContract.UserEntry.COL_ORCAMENTO))

                listaAtividades.add(
                    Atividade(
                        id,
                        nome,
                        descricao,
                        dataInicio,
                        dataTermino,
                        codigoResponsavel,
                        aprovado,
                        finalizada,
                        orcamento
                    )
                )
            }
        }
        cursor.close()
        db.close()
        atividadeAdapter.notifyDataSetChanged()
    }

    private fun editarAtividade(atividade: Atividade) {
        val intent = Intent(this, EditarAtividadeActivity::class.java)
        intent.putExtra("atividade_id", atividade.id)
        intent.putExtra("atividade_nome", atividade.nome)
        intent.putExtra("atividade_descricao", atividade.descricao)
        intent.putExtra("atividade_data_inicio", atividade.dataInicio)
        intent.putExtra("atividade_data_termino", atividade.dataTermino)
        intent.putExtra("atividade_codigo_responsavel", atividade.responsavel)
        intent.putExtra("atividade_aprovado", atividade.aprovado)
        intent.putExtra("atividade_finalizada", atividade.finalizado)
        intent.putExtra("atividade_orcamento", atividade.orcamento)
        startActivity(intent)
        android.widget.Toast.makeText(this, "Editar: ${atividade.nome}", android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun excluirAtividade(atividade: Atividade) {
        val db = dbHelper.writableDatabase
        val whereClause = "${android.provider.BaseColumns._ID} = ?"
        val whereArgs = arrayOf(atividade.id.toString())
        val deletedRows = db.delete(com.example.mpi.data.AtividadeContract.UserEntry.TABLE_NAME, whereClause, whereArgs)
        if (deletedRows > 0) {
            listaAtividades.remove(atividade)
            atividadeAdapter.notifyDataSetChanged()
            android.widget.Toast.makeText(this, "Atividade '${atividade.nome}' excluída com sucesso!", android.widget.Toast.LENGTH_SHORT).show()
        } else {
            android.widget.Toast.makeText(this, "Erro ao excluir a atividade.", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
}