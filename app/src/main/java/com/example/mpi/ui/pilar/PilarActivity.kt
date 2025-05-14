package com.example.mpi.ui.pilar

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mpi.data.Pilar
import com.example.mpi.data.PilarDbHelper
import com.example.mpi.databinding.ActivityPilarBinding
import com.example.mpi.ui.pilar.EditarPilarActivity

class PilarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPilarBinding
    private lateinit var dbHelper: PilarDbHelper
    private lateinit var pilarAdapter: PilarAdapter
    private val listaPilares = mutableListOf<Pilar>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPilarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = PilarDbHelper(this)
        dbHelper.readableDatabase.close()


        binding.recyclerViewPilares.layoutManager = LinearLayoutManager(this)
        pilarAdapter = PilarAdapter(listaPilares, { pilar -> editarPilar(pilar) }, { pilar -> excluirPilar(pilar) })
        binding.recyclerViewPilares.adapter = pilarAdapter

        carregarPilares()

        binding.btnAdicionarPilar.setOnClickListener {
            android.widget.Toast.makeText(this, "Adicionar novo pilar", android.widget.Toast.LENGTH_SHORT).show()
            val intent = android.content.Intent(this, cadastroPilar::class.java)
            startActivity(intent)
        }
        binding.btnVoltar.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        carregarPilares()
    }

    private fun carregarPilares() {
        listaPilares.clear()
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            android.provider.BaseColumns._ID,
            com.example.mpi.data.PilarContract.UserEntry.COL_NOME,
            com.example.mpi.data.PilarContract.UserEntry.COL_DESCRICAO,
            com.example.mpi.data.PilarContract.UserEntry.COL_DATAI,
            com.example.mpi.data.PilarContract.UserEntry.COL_DATAT,
            com.example.mpi.data.PilarContract.UserEntry.COL_APROVADO,
            com.example.mpi.data.PilarContract.UserEntry.COL_PERCENTUAL
        )

        val cursor = db.query(
            com.example.mpi.data.PilarContract.UserEntry.TABLE_NAME,
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
                val nome = getString(getColumnIndexOrThrow(com.example.mpi.data.PilarContract.UserEntry.COL_NOME))
                val descricao = getString(getColumnIndexOrThrow(com.example.mpi.data.PilarContract.UserEntry.COL_DESCRICAO))
                val dataInicio = getString(getColumnIndexOrThrow(com.example.mpi.data.PilarContract.UserEntry.COL_DATAI))
                val dataTermino = getString(getColumnIndexOrThrow(com.example.mpi.data.PilarContract.UserEntry.COL_DATAT))
                val aprovado = getInt(getColumnIndexOrThrow(com.example.mpi.data.PilarContract.UserEntry.COL_APROVADO)) > 0
                val percentual = getDouble(getColumnIndexOrThrow(com.example.mpi.data.PilarContract.UserEntry.COL_PERCENTUAL))

                listaPilares.add(
                    Pilar(
                        id,
                        nome,
                        descricao,
                        dataInicio,
                        dataTermino,
                        aprovado,
                        percentual
                    )
                )
            }
        }
        cursor.close()
        db.close()
        pilarAdapter.notifyDataSetChanged()
    }

    private fun editarPilar(pilar: Pilar) {
        // Aqui está a Intent corrigida:
        val intent = Intent(this, EditarPilarActivity::class.java) // Substitua se o pacote for diferente
        intent.putExtra("pilar_id", pilar.id)
        intent.putExtra("pilar_nome", pilar.nome)
        intent.putExtra("pilar_descricao", pilar.descricao)
        intent.putExtra("pilar_data_inicio", pilar.dataInicio)
        intent.putExtra("pilar_data_termino", pilar.dataTermino)
        intent.putExtra("pilar_aprovado", pilar.aprovado)
        intent.putExtra("pilar_percentual", pilar.percentual)
        startActivity(intent)
        android.widget.Toast.makeText(this, "Editar: ${pilar.nome}", android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun excluirPilar(pilar: Pilar) {
        // Aqui você implementará a lógica para excluir o pilar
        val db = dbHelper.writableDatabase
        val whereClause = "${android.provider.BaseColumns._ID} = ?"
        val whereArgs = arrayOf(pilar.id.toString())
        val deletedRows = db.delete(com.example.mpi.data.PilarContract.UserEntry.TABLE_NAME, whereClause, whereArgs)
        if (deletedRows > 0) {
            listaPilares.remove(pilar)
            pilarAdapter.notifyDataSetChanged()
            android.widget.Toast.makeText(this, "Pilar '${pilar.nome}' excluído com sucesso!", android.widget.Toast.LENGTH_SHORT).show()
        } else {
            android.widget.Toast.makeText(this, "Erro ao excluir o pilar.", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
}