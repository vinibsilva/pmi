package com.example.mpi.ui.pilar

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mpi.databinding.ActivityPilarBinding
import com.example.mpi.ui.pilar.cadastroPilar
import com.example.mpi.ui.pilar.EditarPilarActivity
import com.example.mpi.data.DatabaseHelper
import android.util.Log
import com.example.mpi.data.Pilar


class PilarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPilarBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var pilarAdapter: PilarAdapter
    private val listaPilares = mutableListOf<Pilar>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPilarBinding.inflate(layoutInflater)
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

        binding.recyclerViewPilares.layoutManager = LinearLayoutManager(this)
        pilarAdapter = PilarAdapter(listaPilares, { pilar -> editarPilar(pilar) }, { pilar -> excluirPilar(pilar) })
        binding.recyclerViewPilares.adapter = pilarAdapter

        carregarPilares()

        binding.btnAdicionarPilar.setOnClickListener {
            val intent = Intent(this, cadastroPilar::class.java)
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
        carregarPilares()
    }

    private fun carregarPilares() {
        listaPilares.clear()
        val db = dbHelper.readableDatabase


        val projection = arrayOf(
            DatabaseHelper.COLUMN_PILAR_ID,
            DatabaseHelper.COLUMN_PILAR_NOME,
            DatabaseHelper.COLUMN_PILAR_DESCRICAO,
            DatabaseHelper.COLUMN_PILAR_DATA_INICIO,
            DatabaseHelper.COLUMN_PILAR_DATA_TERMINO,
            DatabaseHelper.COLUMN_PILAR_IS_APROVADO,
            DatabaseHelper.COLUMN_PILAR_PERCENTUAL,
            DatabaseHelper.COLUMN_PILAR_ID_CALENDARIO,
            DatabaseHelper.COLUMN_PILAR_ID_USUARIO
        )

        val cursor = db.query(
            DatabaseHelper.TABLE_PILAR,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_ID))
                val nome = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_NOME))
                val descricao = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_DESCRICAO))
                val dataInicio = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_DATA_INICIO))
                val dataTermino = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_DATA_TERMINO))
                val aprovado = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_IS_APROVADO)) > 0
                val percentual = getDouble(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_PERCENTUAL))
                val idCalendario = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_ID_CALENDARIO))
                val idUsuario = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_ID_USUARIO))

                listaPilares.add(
                    Pilar(
                        id,
                        nome,
                        descricao,
                        dataInicio,
                        dataTermino,
                        aprovado,
                        percentual,
                        idCalendario,
                        idUsuario
                    )
                )
            }
        }
        cursor.close()
        db.close()
        pilarAdapter.notifyDataSetChanged()
    }

    private fun editarPilar(pilar: Pilar) {
        val intent = Intent(this, EditarPilarActivity::class.java)
        intent.putExtra("pilar_id", pilar.id)
        intent.putExtra("pilar_nome", pilar.nome)
        intent.putExtra("pilar_descricao", pilar.descricao)
        intent.putExtra("pilar_data_inicio", pilar.dataInicio)
        intent.putExtra("pilar_data_termino", pilar.dataTermino)
        intent.putExtra("pilar_aprovado", pilar.aprovado)
        intent.putExtra("pilar_percentual", pilar.percentual)
        intent.putExtra("pilar_id_calendario", pilar.idCalendario)
        intent.putExtra("pilar_id_usuario", pilar.idUsuario)
        startActivity(intent)
        android.widget.Toast.makeText(this, "Editar: ${pilar.nome}", android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun excluirPilar(pilar: Pilar) {
        val db = dbHelper.writableDatabase
        val whereClause = "${DatabaseHelper.COLUMN_PILAR_ID} = ?"
        val whereArgs = arrayOf(pilar.id.toString())
        val deletedRows = db.delete(DatabaseHelper.TABLE_PILAR, whereClause, whereArgs)
        if (deletedRows > 0) {
            listaPilares.remove(pilar)
            pilarAdapter.notifyDataSetChanged()

            //verificando se o Pilar era o único existente, se sim o registro da tabela calendário sera apagado
            if (listaPilares.isEmpty()) {
                val idCalendarioExcluir = pilar.idCalendario
                val whereClauseCalendario = "${DatabaseHelper.COLUMN_CALENDARIO_ID} = ?"
                val whereArgsCalendario = arrayOf(idCalendarioExcluir.toString())
                db.delete(
                    DatabaseHelper.TABLE_CALENDARIO,
                    whereClauseCalendario,
                    whereArgsCalendario
                )
            }

            android.widget.Toast.makeText(this, "Pilar '${pilar.nome}' excluído com sucesso!", android.widget.Toast.LENGTH_SHORT).show()
        } else {
            android.widget.Toast.makeText(this, "Erro ao excluir o pilar.", android.widget.Toast.LENGTH_SHORT).show()
        }
        db.close()
    }
}