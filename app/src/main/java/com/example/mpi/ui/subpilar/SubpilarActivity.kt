package com.example.mpi.ui.subpilar

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mpi.data.Subpilar
import com.example.mpi.data.SubpilarDbHelper
import com.example.mpi.databinding.ActivitySubpilarBinding
import com.example.mpi.ui.pilar.EditarPilarActivity


class SubpilarActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySubpilarBinding
    private lateinit var dbHelper: SubpilarDbHelper
    private lateinit var subpilarAdapter: SubpilarAdapter
    private val listaSubpilares = mutableListOf<Subpilar>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubpilarBinding.inflate(layoutInflater)
        setContentView(binding.root)


        dbHelper = SubpilarDbHelper(this)
        dbHelper.readableDatabase.close()

        binding.recyclerViewSubpilares.layoutManager = LinearLayoutManager(this)
        subpilarAdapter = SubpilarAdapter(listaSubpilares, { subpilar -> editarSubpilar(subpilar) }, { subpilar -> excluirSubpilar(subpilar) })
        binding.recyclerViewSubpilares.adapter = subpilarAdapter

        carregarSubpilares()

        binding.btnAdicionarSubpilar.setOnClickListener {
            android.widget.Toast.makeText(this, "Adicionar novo subpilar", android.widget.Toast.LENGTH_SHORT).show()
            val intent = android.content.Intent(this, cadastroSubpilar::class.java)
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
            android.provider.BaseColumns._ID,
            com.example.mpi.data.SubpilarContract.UserEntry.COL_NOME,
            com.example.mpi.data.SubpilarContract.UserEntry.COL_DESCRICAO,
            com.example.mpi.data.SubpilarContract.UserEntry.COL_DATAI,
            com.example.mpi.data.SubpilarContract.UserEntry.COL_DATAT,
            com.example.mpi.data.SubpilarContract.UserEntry.COL_APROVADO
        )

        val cursor = db.query(
            com.example.mpi.data.SubpilarContract.UserEntry.TABLE_NAME,
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
                val nome = getString(getColumnIndexOrThrow(com.example.mpi.data.SubpilarContract.UserEntry.COL_NOME))
                val descricao = getString(getColumnIndexOrThrow(com.example.mpi.data.SubpilarContract.UserEntry.COL_DESCRICAO))
                val dataInicio = getString(getColumnIndexOrThrow(com.example.mpi.data.SubpilarContract.UserEntry.COL_DATAI))
                val dataTermino = getString(getColumnIndexOrThrow(com.example.mpi.data.SubpilarContract.UserEntry.COL_DATAT))
                val aprovado = getInt(getColumnIndexOrThrow(com.example.mpi.data.SubpilarContract.UserEntry.COL_APROVADO)) > 0

                listaSubpilares.add(
                    Subpilar(
                        id,
                        nome,
                        descricao,
                        dataInicio,
                        dataTermino,
                        aprovado
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
        startActivity(intent)
        android.widget.Toast.makeText(this, "Editar: ${subpilar.nome}", android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun excluirSubpilar(subpilar: Subpilar) {
        val db = dbHelper.writableDatabase
        val whereClause = "${android.provider.BaseColumns._ID} = ?"
        val whereArgs = arrayOf(subpilar.id.toString())
        val deletedRows = db.delete(com.example.mpi.data.SubpilarContract.UserEntry.TABLE_NAME, whereClause, whereArgs)
        if (deletedRows > 0) {
            listaSubpilares.remove(subpilar)
            subpilarAdapter.notifyDataSetChanged()
            android.widget.Toast.makeText(this, "Subpilar '${subpilar.nome}' excluído com sucesso!", android.widget.Toast.LENGTH_SHORT).show()
        } else {
            android.widget.Toast.makeText(this, "Erro ao excluir o subpilar.", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
}