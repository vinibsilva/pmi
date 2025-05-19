package com.example.mpi.ui.acao

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mpi.databinding.ActivityAcaoBinding
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.data.Acao

class AcaoActivity : AppCompatActivity() {

    val USUARIO_ANALISTA = "ANALISTA"
    val USUARIO_COORDENADOR = "COORDENADOR"
    val USUARIO_GESTOR = "GESTOR"

    private lateinit var binding: ActivityAcaoBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var acaoAdapter: AcaoAdapter
    private val listaAcoes = mutableListOf<Acao>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAcaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ////////////////////// Carregando informações do usuário////////////////////////////////
        val intentExtra = intent
        val idUsuario = intentExtra.getIntExtra("idUsuario", 999999)
        val nomeUsuario = intentExtra.getStringExtra("nomeUsuario") ?: "Nome de usuário desconhecido"
        val tipoUsuario = intentExtra.getStringExtra("tipoUsuario") ?: "Tipo de usuário desconhecido"
        val tag = "PilarActivityLog"
        val mensagemLog = "PilarActivity iniciada - ID Usuário: $idUsuario, Nome: $nomeUsuario"
        Log.d(tag, mensagemLog)
        ////////////////////////////////////////////////////////////////////////////////

        dbHelper = DatabaseHelper(this)

        binding.recyclerViewAcoes.layoutManager = LinearLayoutManager(this)
        acaoAdapter = AcaoAdapter(listaAcoes,
            onEditarClicked = { acao -> editarAcao(acao) },
            onExcluirClicked = { acao -> excluirAcao(acao) })
        binding.recyclerViewAcoes.adapter = acaoAdapter

        if (tipoUsuario.uppercase() == USUARIO_GESTOR) {
            binding.btnAdicionarAcao.visibility = View.GONE
        }

        carregarAcoes()

        binding.btnAdicionarAcao.setOnClickListener {
            val intent = Intent(this, CadastroAcaoActivity::class.java)
            intent.putExtra("idUsuario", idUsuario)
            intent.putExtra("nomeUsuario", nomeUsuario)
            intent.putExtra("tipoUsuario", tipoUsuario)
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
            DatabaseHelper.COLUMN_ACAO_ID,
            DatabaseHelper.COLUMN_ACAO_NOME,
            DatabaseHelper.COLUMN_ACAO_DESCRICAO,
            DatabaseHelper.COLUMN_ACAO_DATA_INICIO,
            DatabaseHelper.COLUMN_ACAO_DATA_TERMINO,
            DatabaseHelper.COLUMN_ACAO_RESPONSAVEL,
            DatabaseHelper.COLUMN_ACAO_IS_APROVADO,
            DatabaseHelper.COLUMN_ACAO_IS_FINALIZADO,
            DatabaseHelper.COLUMN_ACAO_ID_PILAR,
            DatabaseHelper.COLUMN_ACAO_ID_SUBPILAR,
            DatabaseHelper.COLUMN_ACAO_ID_USUARIO
        )

        val cursor = db.query(
            DatabaseHelper.TABLE_ACAO,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID))
                val nome = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_NOME))
                val descricao = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DESCRICAO))
                val dataInicio = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_INICIO))
                val dataTermino = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_TERMINO))
                val responsavel = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_RESPONSAVEL))
                val aprovado = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_IS_APROVADO)) > 0
                val finalizado = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_IS_FINALIZADO)) > 0
                val id_pilar = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_PILAR))
                val id_subpilar = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_SUBPILAR))
                val id_usuario = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_USUARIO))


                listaAcoes.add(
                    Acao(
                        id,
                        nome,
                        descricao,
                        dataInicio,
                        dataTermino,
                        responsavel,
                        aprovado,
                        finalizado,
                        id_pilar,
                        id_subpilar,
                        id_usuario
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
        intent.putExtra("acao_id_pilar", acao.id_pilar)
        intent.putExtra("acao_id_subpilar", acao.id_subpilar)
        intent.putExtra("acao_id_usuario", acao.id_usuario)
        startActivity(intent)
    }

    private fun excluirAcao(acao: Acao) {
        val db = dbHelper.writableDatabase
        val whereClause = "${DatabaseHelper.COLUMN_ACAO_ID} = ?"
        val whereArgs = arrayOf(acao.id.toString())
        val deletedRows = db.delete(DatabaseHelper.TABLE_ACAO, whereClause, whereArgs)
        db.close()
        if (deletedRows > 0) {
            listaAcoes.remove(acao)
            acaoAdapter.notifyDataSetChanged()
            Toast.makeText(this, "Ação '${acao.nome}' excluída com sucesso!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Erro ao excluir a ação.", Toast.LENGTH_SHORT).show()
        }
    }
}
