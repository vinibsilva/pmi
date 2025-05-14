package com.example.mpi.ui.pilar

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mpi.R
import com.example.mpi.databinding.ActivityCadastroPilarBinding
import com.example.mpi.data.PilarDbHelper
import com.example.mpi.data.PilarContract

class cadastroPilar : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroPilarBinding
    private lateinit var dbHelper: PilarDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCadastroPilarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = PilarDbHelper(this)

        binding.btnconfirmarCadastro.setOnClickListener{
            val nome = binding.etnomePilar.text.toString()
            val descricao = binding.etdescricaoPilar.text.toString()
            val dataInicio = binding.etdataInicio.text.toString().trim()
            val dataTermino = binding.etdataTermino.text.toString().trim()

            if (nome.isEmpty() || descricao.isEmpty() || dataInicio.isEmpty() || dataTermino.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val db = dbHelper.writableDatabase
            val values = android.content.ContentValues().apply {
                put(PilarContract.UserEntry.COL_NOME, nome)
                put(PilarContract.UserEntry.COL_DESCRICAO, descricao)
                put(PilarContract.UserEntry.COL_DATAI, dataInicio)
                put(PilarContract.UserEntry.COL_DATAT, dataTermino)

            }

            val newRowId = db.insert(PilarContract.UserEntry.TABLE_NAME, null, values)
            if (newRowId == -1L) {
                Toast.makeText(this, "Erro ao cadastrar — O Pilar já existe?", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Pilar cadastrado com sucesso! Atualize a página de listagem.", Toast.LENGTH_SHORT).show()
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