package com.example.mpi.ui.atividade

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mpi.databinding.ActivityAtividadeBinding
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.data.Atividade

class AtividadeActivity : AppCompatActivity() {

    val USUARIO_ANALISTA = "ANALISTA"
    val USUARIO_COORDENADOR = "COORDENADOR"
    val USUARIO_GESTOR = "GESTOR"

    private lateinit var binding: ActivityAtividadeBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var atividadeAdapter: AtividadeAdapter
    private val listaAtividades = mutableListOf<Atividade>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAtividadeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        dbHelper = DatabaseHelper(this)

        ////////////////////// Carregando informações do usuário////////////////////////////////
        val intentExtra = intent
        val idUsuario = intentExtra.getIntExtra("idUsuario", 999999)
        val nomeUsuario = intentExtra.getStringExtra("nomeUsuario") ?: "Nome de usuário desconhecido"
        val tipoUsuario = intentExtra.getStringExtra("tipoUsuario") ?: "Tipo de usuário desconhecido"
        val tag = "PilarActivityLog"
        val mensagemLog = "PilarActivity iniciada - ID Usuário: $idUsuario, Nome: $nomeUsuario"
        Log.d(tag, mensagemLog)
        ////////////////////////////////////////////////////////////////////////////////

        binding.recyclerViewAtividades.layoutManager = LinearLayoutManager(this)
        atividadeAdapter = AtividadeAdapter(
            listaAtividades,
            { atividade -> editarAtividade(atividade) },
            { atividade -> excluirAtividade(atividade) })
        binding.recyclerViewAtividades.adapter = atividadeAdapter

        if (tipoUsuario.uppercase() == USUARIO_GESTOR) {
            binding.btnCadastrarAtividade.visibility = View.GONE
        }

        carregarAtividades()

        binding.btnCadastrarAtividade.setOnClickListener {
            val intent = Intent(this, CadastroAtividadeActivity::class.java)
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
        carregarAtividades()
    }

    private fun carregarAtividades() {
        listaAtividades.clear()
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            DatabaseHelper.COLUMN_ATIVIDADE_ID,
            DatabaseHelper.COLUMN_ATIVIDADE_NOME,
            DatabaseHelper.COLUMN_ATIVIDADE_DESCRICAO,
            DatabaseHelper.COLUMN_ATIVIDADE_DATA_INICIO,
            DatabaseHelper.COLUMN_ATIVIDADE_DATA_TERMINO,
            DatabaseHelper.COLUMN_ATIVIDADE_RESPONSAVEL,
            DatabaseHelper.COLUMN_ATIVIDADE_IS_APROVADO,
            DatabaseHelper.COLUMN_ATIVIDADE_IS_FINALIZADO,
            DatabaseHelper.COLUMN_ATIVIDADE_ORCAMENTO,
            DatabaseHelper.COLUMN_ATIVIDADE_ID_ACAO,
            DatabaseHelper.COLUMN_ATIVIDADE_ID_USUARIO
        )

        val cursor = db.query(
            DatabaseHelper.TABLE_ATIVIDADE,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_ID))
                val nome = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_NOME))
                val descricao = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_DESCRICAO))
                val dataInicio = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_DATA_INICIO))
                val dataTermino = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_DATA_TERMINO))
                val responsavel = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_RESPONSAVEL))
                val aprovado = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_IS_APROVADO)) > 0
                val finalizado = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_IS_FINALIZADO)) > 0
                val orcamento = getDouble(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_ORCAMENTO))
                val idAcao = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_ID_ACAO))
                val idUsuario = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_ID_USUARIO))

                listaAtividades.add(
                    Atividade(
                        id,
                        nome,
                        descricao,
                        dataInicio,
                        dataTermino,
                        responsavel,
                        aprovado,
                        finalizado,
                        orcamento,
                        idAcao,
                        idUsuario
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
        intent.putExtra("atividade_id_acao", atividade.idAcao)
        intent.putExtra("atividade_id_usuario", atividade.idUsuario)
        startActivity(intent)
    }

    private fun excluirAtividade(atividade: Atividade) {
        val db = dbHelper.writableDatabase
        val whereClause = "${DatabaseHelper.COLUMN_ATIVIDADE_ID} = ?"
        val whereArgs = arrayOf(atividade.id.toString())
        val deletedRows = db.delete(DatabaseHelper.TABLE_ATIVIDADE, whereClause, whereArgs)
        db.close()
        if (deletedRows > 0) {
            listaAtividades.remove(atividade)
            atividadeAdapter.notifyDataSetChanged()
            Toast.makeText(this, "Atividade '${atividade.nome}' excluída com sucesso!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Erro ao excluir a atividade.", Toast.LENGTH_SHORT).show()
        }
    }
}
