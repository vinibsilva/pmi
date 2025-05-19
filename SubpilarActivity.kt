package com.example.mpi.ui.subpilar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.databinding.ActivitySubpilarBinding
import com.example.mpi.data.Subpilar
import com.example.mpi.ui.subpilar.EditarSubpilarActivity


class SubpilarActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySubpilarBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var subpilarAdapter: SubpilarAdapter
    private val listaSubpilares = mutableListOf<Subpilar>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubpilarBinding.inflate(layoutInflater)
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

        binding.recyclerViewSubpilares.layoutManager = LinearLayoutManager(this)
        subpilarAdapter = SubpilarAdapter(
            listaSubpilares,
            { subpilar -> editarSubpilar(subpilar) },
            { subpilar -> excluirSubpilar(subpilar) })
        binding.recyclerViewSubpilares.adapter = subpilarAdapter

        carregarSubpilares()

        binding.btnAdicionarSubpilar.setOnClickListener {
            val intent = Intent(this, cadastroSubpilar::class.java)
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
        carregarSubpilares()
    }

    private fun carregarSubpilares() {
        listaSubpilares.clear()
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            DatabaseHelper.COLUMN_SUBPILAR_ID,
            DatabaseHelper.COLUMN_SUBPILAR_NOME,
            DatabaseHelper.COLUMN_SUBPILAR_DESCRICAO,
            DatabaseHelper.COLUMN_SUBPILAR_DATA_INICIO,
            DatabaseHelper.COLUMN_SUBPILAR_DATA_TERMINO,
            DatabaseHelper.COLUMN_SUBPILAR_IS_APROVADO,
            DatabaseHelper.COLUMN_SUBPILAR_ID_PILAR,
            DatabaseHelper.COLUMN_SUBPILAR_ID_USUARIO
        )

        val cursor = db.query(
            DatabaseHelper.TABLE_SUBPILAR,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_ID))
                val nome = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_NOME))
                val descricao = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_DESCRICAO))
                val dataInicio = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_DATA_INICIO))
                val dataTermino = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_DATA_TERMINO))
                val aprovado = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_IS_APROVADO)) > 0
                val idPilar = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_ID_PILAR))
                val idUsuario = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_ID_USUARIO))

                listaSubpilares.add(
                    Subpilar(
                        id,
                        nome,
                        descricao,
                        dataInicio,
                        dataTermino,
                        aprovado,
                        idPilar,
                        idUsuario
                    )
                )
            }
        }
        cursor.close()
        db.close()
        subpilarAdapter.notifyDataSetChanged()
    }

    private fun editarSubpilar(subpilar: Subpilar) {
        val intent = Intent(this, EditarSubpilarActivity::class.java)
        intent.putExtra("subpilar_id", subpilar.id)
        intent.putExtra("subpilar_nome", subpilar.nome)
        intent.putExtra("subpilar_descricao", subpilar.descricao)
        intent.putExtra("subpilar_data_inicio", subpilar.dataInicio)
        intent.putExtra("subpilar_data_termino", subpilar.dataTermino)
        intent.putExtra("subpilar_aprovado", subpilar.aprovado)
        intent.putExtra("subpilar_id_pilar", subpilar.idPilar)
        intent.putExtra("subpilar_id_usuario", subpilar.idUsuario)
        startActivity(intent)
    }

    private fun excluirSubpilar(subpilar: Subpilar) {
        val db = dbHelper.writableDatabase
        val whereClause = "${DatabaseHelper.COLUMN_SUBPILAR_ID} = ?"
        val whereArgs = arrayOf(subpilar.id.toString())
        val deletedRows = db.delete(DatabaseHelper.TABLE_SUBPILAR, whereClause, whereArgs)
        if (deletedRows > 0) {
            listaSubpilares.remove(subpilar)
            subpilarAdapter.notifyDataSetChanged()
            android.widget.Toast.makeText(this, "Subpilar '${subpilar.nome}' excluído com sucesso!", android.widget.Toast.LENGTH_SHORT).show()
        } else {
            android.widget.Toast.makeText(this, "Erro ao excluir o subpilar.", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
}