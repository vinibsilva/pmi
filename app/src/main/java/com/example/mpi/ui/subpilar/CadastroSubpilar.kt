package com.example.mpi.ui.subpilar

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mpi.R
import com.example.mpi.databinding.ActivityCadastroSubpilarBinding
import com.example.mpi.data.SubpilarDbHelper
import com.example.mpi.data.SubpilarContract


class cadastroSubpilar : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroSubpilarBinding
    private lateinit var dbHelper: SubpilarDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCadastroSubpilarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = SubpilarDbHelper(this)

        binding.btnconfirmarCadastro.setOnClickListener{
            val nome = binding.etnomeSubpilar.text.toString()
            val descricao = binding.etdescricaoSubpilar.text.toString()
            val dataInicio = binding.etdataInicio.text.toString().trim()
            val dataTermino = binding.etdataTermino.text.toString().trim()

            if (nome.isEmpty() || descricao.isEmpty() || dataInicio.isEmpty() || dataTermino.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val db = dbHelper.writableDatabase
            val values = android.content.ContentValues().apply {
                put(SubpilarContract.UserEntry.COL_NOME, nome)
                put(SubpilarContract.UserEntry.COL_DESCRICAO, descricao)
                put(SubpilarContract.UserEntry.COL_DATAI, dataInicio)
                put(SubpilarContract.UserEntry.COL_DATAT, dataTermino)

            }

            val newRowId = db.insert(SubpilarContract.UserEntry.TABLE_NAME, null, values)
            if (newRowId == -1L) {
                Toast.makeText(this, "Erro ao cadastrar — O Subpilar já existe?", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Subpilar cadastrado com sucesso! Atualize a página de listagem.", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }
        }

        binding.btnVoltar.setOnClickListener{
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}