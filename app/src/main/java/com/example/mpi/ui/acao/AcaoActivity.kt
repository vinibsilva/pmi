package com.example.mpi.ui.acao

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mpi.data.Acao
import com.example.mpi.data.AcaoDbHelper
import com.example.mpi.databinding.ActivityAcaoBinding
import com.example.mpi.ui.acao.AcaoAdapter
import com.example.mpi.ui.acao.CadastroAcaoActivity
import com.example.mpi.ui.acao.EditarAcaoActivity


class AcaoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAcaoBinding
    private lateinit var dbHelper: AcaoDbHelper
    private lateinit var acaoAdapter: AcaoAdapter
    private val listaAcoes = mutableListOf<Acao>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAcaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = AcaoDbHelper(this)


        binding.recyclerViewAcoes.layoutManager = LinearLayoutManager(this)
        acaoAdapter = AcaoAdapter(listaAcoes, { acao -> editarAcao(acao) }, { acao -> excluirAcao(acao) })
        binding.recyclerViewAcoes.adapter = acaoAdapter

        carregarAcoes()

        binding.btnAdicionarAcao.setOnClickListener {
            android.widget.Toast.makeText(this, "Adicionar nova ação", android.widget.Toast.LENGTH_SHORT).show()
            val intent = android.content.Intent(this, CadastroAcaoActivity::class.java)
            startActivity(intent)
        }
        binding.btnVoltar.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        carregarAcoes()
    }

    private fun carregarAcoes() {
        listaAcoes.clear()
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            android.provider.BaseColumns._ID,
            com.example.mpi.data.AcaoContract.UserEntry.COL_NOME,
            com.example.mpi.data.AcaoContract.UserEntry.COL_DESCRICAO,
            com.example.mpi.data.AcaoContract.UserEntry.COL_DATAI,
            com.example.mpi.data.AcaoContract.UserEntry.COL_DATAT,
            com.example.mpi.data.AcaoContract.UserEntry.COL_RESPONSAVEL,
            com.example.mpi.data.AcaoContract.UserEntry.COL_APROVADO,
            com.example.mpi.data.AcaoContract.UserEntry.COL_FINALIZADO
        )

        val cursor = db.query(
            com.example.mpi.data.AcaoContract.UserEntry.TABLE_NAME,
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
                val nome = getString(getColumnIndexOrThrow(com.example.mpi.data.AcaoContract.UserEntry.COL_NOME))
                val descricao = getString(getColumnIndexOrThrow(com.example.mpi.data.AcaoContract.UserEntry.COL_DESCRICAO))
                val dataInicio = getString(getColumnIndexOrThrow(com.example.mpi.data.AcaoContract.UserEntry.COL_DATAI))
                val dataTermino = getString(getColumnIndexOrThrow(com.example.mpi.data.AcaoContract.UserEntry.COL_DATAT))
                val codigoResponsavel = getInt(getColumnIndexOrThrow(com.example.mpi.data.AcaoContract.UserEntry.COL_RESPONSAVEL))
                val aprovado = getInt(getColumnIndexOrThrow(com.example.mpi.data.AcaoContract.UserEntry.COL_APROVADO)) > 0
                val finalizada = getInt(getColumnIndexOrThrow(com.example.mpi.data.AcaoContract.UserEntry.COL_FINALIZADO)) > 0

                listaAcoes.add(
                    Acao(
                        id,
                        nome,
                        descricao,
                        dataInicio,
                        dataTermino,
                        codigoResponsavel,
                        aprovado,
                        finalizada
                    )
                )
            }
        }
        cursor.close()
        db.close()
        acaoAdapter.notifyDataSetChanged()
    }

    private fun editarAcao(acao: Acao) {
        val intent = Intent(this, EditarAcaoActivity::class.java)
        intent.putExtra("acao_id", acao.id)
        intent.putExtra("acao_nome", acao.nome)
        intent.putExtra("acao_descricao", acao.descricao)
        intent.putExtra("acao_data_inicio", acao.dataInicio)
        intent.putExtra("acao_data_termino", acao.dataTermino)
        intent.putExtra("acao_codigo_responsavel", acao.responsavel)
        intent.putExtra("acao_aprovado", acao.aprovado)
        intent.putExtra("acao_finalizada", acao.finalizado)
        startActivity(intent)
        android.widget.Toast.makeText(this, "Editar: ${acao.nome}", android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun excluirAcao(acao: Acao) {
        val db = dbHelper.writableDatabase
        val whereClause = "${android.provider.BaseColumns._ID} = ?"
        val whereArgs = arrayOf(acao.id.toString())
        val deletedRows = db.delete(com.example.mpi.data.AcaoContract.UserEntry.TABLE_NAME, whereClause, whereArgs)
        if (deletedRows > 0) {
            listaAcoes.remove(acao)
            acaoAdapter.notifyDataSetChanged()
            android.widget.Toast.makeText(this, "Ação '${acao.nome}' excluída com sucesso!", android.widget.Toast.LENGTH_SHORT).show()
        } else {
            android.widget.Toast.makeText(this, "Erro ao excluir a ação.", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
}