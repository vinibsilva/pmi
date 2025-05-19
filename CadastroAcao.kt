package com.example.mpi.ui.acao

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.databinding.ActivityCadastroAcaoBinding
import com.example.mpi.data.Pilar
import com.example.mpi.data.Subpilar
import com.example.mpi.data.Usuario
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CadastroAcaoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroAcaoBinding
    private lateinit var dbHelper: DatabaseHelper
    private var listaPilaresNomes = mutableListOf<String>()
    private var listaPilaresObjetos = mutableListOf<Pilar>()
    private var listaSubpilaresNomes = mutableListOf<String>()
    private var listaSubpilaresObjetos = mutableListOf<Subpilar>()
    private var idVinculoSelecionado: Int = -1
    private var tipoVinculo: String = ""
    private lateinit var listaUsuariosNomes: MutableList<String>
    private lateinit var listaUsuariosObjetos: MutableList<Usuario>
    private var idResponsavelSelecionado: Int = -1
    private var idUsuarioRecebido: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroAcaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        carregarUsuariosNoSpinner()

        // Recebendo os dados de usuário
        val extras = intent.extras
        if (extras != null) {
            idUsuarioRecebido = extras.getInt("idUsuario", 999999)
            val nomeUsuario = extras.getString("nomeUsuario") ?: "Nome de usuário desconhecido"
            val tipoUsuario = extras.getString("tipoUsuario") ?: "Tipo de usuário desconhecido"
        }


        binding.spinnerVinculo.isEnabled = false
        binding.spinnerVinculo.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("Selecione o tipo de vínculo"))


        binding.radioGroupVinculo.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioButton = findViewById<RadioButton>(checkedId)
            tipoVinculo = selectedRadioButton.text.toString().lowercase()
            binding.spinnerVinculo.isEnabled = true
            idVinculoSelecionado = -1 // Resetar a seleção anterior

            if (tipoVinculo == "pilar") {
                carregarPilaresNoSpinner()
            } else if (tipoVinculo == "subpilar") {
                carregarSubpilaresNoSpinner()
            }
        }


        binding.spinnerVinculo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    if (tipoVinculo == "pilar") {
                        idVinculoSelecionado = listaPilaresObjetos[position - 1].id
                    } else if (tipoVinculo == "subpilar") {
                        idVinculoSelecionado = listaSubpilaresObjetos[position - 1].id
                    }
                } else {
                    idVinculoSelecionado = -1
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                idVinculoSelecionado = -1
            }
        }

        binding.confirmarCadastro.setOnClickListener {
            val nome = binding.nomeAcao.text.toString()
            val descricao = binding.descricaoAcao.text.toString()
            val dataInicio = binding.dataInicio.text.toString().trim()
            val dataTermino = binding.dataTermino.text.toString().trim()

            if (nome.isEmpty() || descricao.isEmpty() || dataInicio.isEmpty() || dataTermino.isEmpty() || idVinculoSelecionado == -1L.toInt()) {
                Toast.makeText(this, "Preencha todos os campos e selecione um Pilar ou Subpilar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //Validação das datas informadas:
            val dataInicioFormatada = validarEFormatarDataInicial(dataInicio, tipoVinculo, idVinculoSelecionado)
            val dataTerminoFormatada = validarEFormatarDataFinal(dataTermino, dataInicio, tipoVinculo, idVinculoSelecionado)

            if (dataInicioFormatada == null) {
                Toast.makeText(this, "Data de Início inválida. Use o formato dd/mm/aaaa e respeite as datas do Pilar/Subpilar.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (dataTerminoFormatada == null) {
                Toast.makeText(this, "Data de Término inválida. Use o formato dd/mm/aaaa e respeite as datas do Pilar/Subpilar.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }




            val db = dbHelper.writableDatabase
            val values = android.content.ContentValues().apply {
                put(DatabaseHelper.COLUMN_ACAO_NOME, nome)
                put(DatabaseHelper.COLUMN_ACAO_DESCRICAO, descricao)
                put(DatabaseHelper.COLUMN_ACAO_DATA_INICIO, dataInicio)
                put(DatabaseHelper.COLUMN_ACAO_DATA_TERMINO, dataTermino)
                put(DatabaseHelper.COLUMN_ACAO_RESPONSAVEL, idResponsavelSelecionado)
                put(DatabaseHelper.COLUMN_ACAO_ID_USUARIO, idUsuarioRecebido)

                if (tipoVinculo == "pilar") {
                    put(DatabaseHelper.COLUMN_ACAO_ID_PILAR, idVinculoSelecionado)
                    putNull(DatabaseHelper.COLUMN_ACAO_ID_SUBPILAR)
                } else if (tipoVinculo == "subpilar") {
                    put(DatabaseHelper.COLUMN_ACAO_ID_SUBPILAR, idVinculoSelecionado)
                    putNull(DatabaseHelper.COLUMN_ACAO_ID_PILAR)
                }
            }

            val newRowId = db.insert(DatabaseHelper.TABLE_ACAO, null, values)
            db.close()

            if (newRowId == -1L) {
                Toast.makeText(this, "Erro ao cadastrar — A Ação já existe?", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Ação cadastrada com sucesso! Atualize a página de listagem.", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }
        }

        binding.cancelarCadastro.setOnClickListener {
            finish()
        }
    }

    private fun carregarPilaresNoSpinner() {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(DatabaseHelper.COLUMN_PILAR_ID, DatabaseHelper.COLUMN_PILAR_NOME)
        val cursor = db.query(DatabaseHelper.TABLE_PILAR, projection, null, null, null, null, DatabaseHelper.COLUMN_PILAR_NOME)

        listaPilaresNomes.clear()
        listaPilaresObjetos.clear()
        listaPilaresNomes.add("Selecione o Pilar")

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_ID))
                val nome = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_NOME))
                listaPilaresObjetos.add(Pilar(id, nome, "", "", "", false, 0.0, 0, 0))
                listaPilaresNomes.add(nome)
            }
        }
        cursor.close()
        db.close()

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listaPilaresNomes)
        binding.spinnerVinculo.adapter = adapter
    }

    private fun carregarUsuariosNoSpinner() {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(DatabaseHelper.COLUMN_USUARIO_ID, DatabaseHelper.COLUMN_USUARIO_NOME)
        val cursor = db.query(
            DatabaseHelper.TABLE_USUARIO,
            projection,
            null,
            null,
            null,
            null,
            DatabaseHelper.COLUMN_USUARIO_NOME
        )

        listaUsuariosNomes = mutableListOf()
        listaUsuariosObjetos = mutableListOf()
        listaUsuariosNomes.add("Selecione o Responsável")

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_ID))
                val nome = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_NOME))
                listaUsuariosObjetos.add(Usuario(id, nome, "", "", 0)) // Os outros campos não são necessários aqui
                listaUsuariosNomes.add(nome)
            }
        }
        cursor.close()
        db.close()

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listaUsuariosNomes)
        binding.spinnerResponsavel.adapter = adapter

        binding.spinnerResponsavel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    idResponsavelSelecionado = listaUsuariosObjetos[position - 1].id
                } else {
                    idResponsavelSelecionado = -1
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                idResponsavelSelecionado = -1
            }
        }
    }
    private fun carregarSubpilaresNoSpinner() {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(DatabaseHelper.COLUMN_SUBPILAR_ID, DatabaseHelper.COLUMN_SUBPILAR_NOME)
        val cursor = db.query(DatabaseHelper.TABLE_SUBPILAR, projection, null, null, null, null, DatabaseHelper.COLUMN_SUBPILAR_NOME)

        listaSubpilaresNomes.clear()
        listaSubpilaresObjetos.clear()
        listaSubpilaresNomes.add("Selecione o Subpilar")

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_ID))
                val nome = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_NOME))
                listaSubpilaresObjetos.add(Subpilar(id, nome, "", "", "", false, 0, 0))
                listaSubpilaresNomes.add(nome)
            }
        }
        cursor.close()
        db.close()

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listaSubpilaresNomes)
        binding.spinnerVinculo.adapter = adapter
    }
    private fun validarEFormatarDataInicial(dataAcaoStr: String, tipoVinculo: String, idVinculoSelecionado: Int): String? {
        if (dataAcaoStr.isNullOrEmpty()) {
            return null
        }

        val partesAcao = dataAcaoStr.split("/")
        if (partesAcao.size != 3) {
            return null
        }

        val diaAcaoStr = partesAcao[0]
        val mesAcaoStr = partesAcao[1]
        val anoAcaoStr = partesAcao[2]

        if (diaAcaoStr.length != 2 || mesAcaoStr.length != 2 || anoAcaoStr.length != 4) {
            return null
        }

        if (idVinculoSelecionado != -1L.toInt() && tipoVinculo.isNotEmpty()) {
            val db = dbHelper.readableDatabase
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.isLenient = false

            try {
                if (tipoVinculo == "pilar") {
                    val cursorPilar = db.query(
                        DatabaseHelper.TABLE_PILAR,
                        arrayOf(DatabaseHelper.COLUMN_PILAR_DATA_INICIO, DatabaseHelper.COLUMN_PILAR_DATA_TERMINO),
                        "${DatabaseHelper.COLUMN_PILAR_ID} = ?",
                        arrayOf(idVinculoSelecionado.toString()),
                        null,
                        null,
                        null
                    )
                    cursorPilar?.use {
                        if (it.moveToFirst()) {
                            val dataInicioPilarStr = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_DATA_INICIO))
                            val dataTerminoPilarStr = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_DATA_TERMINO))

                            val dataInicioPilar = sdf.parse(dataInicioPilarStr)
                            val dataTerminoPilar = sdf.parse(dataTerminoPilarStr)
                            val dataAcao = sdf.parse(dataAcaoStr)

                            if (dataAcao.before(dataInicioPilar)) {
                                Toast.makeText(this@CadastroAcaoActivity, "A data de início da Ação não pode ser anterior à data de início do Pilar.", Toast.LENGTH_SHORT).show()
                                return null
                            }
                            if (dataAcao.after(dataTerminoPilar)) {
                                Toast.makeText(this@CadastroAcaoActivity, "A data de início da Ação não pode ser posterior à data de término do Pilar.", Toast.LENGTH_SHORT).show()
                                return null
                            }
                            if (anoAcaoStr != dataInicioPilarStr.split("/")[2]) {
                                Toast.makeText(this@CadastroAcaoActivity, "O ano da data de início da Ação deve ser o mesmo do Pilar.", Toast.LENGTH_SHORT).show()
                                return null
                            }
                        }
                    }
                    cursorPilar?.close()
                } else if (tipoVinculo == "subpilar") {
                    val cursorSubpilar = db.query(
                        DatabaseHelper.TABLE_SUBPILAR,
                        arrayOf(DatabaseHelper.COLUMN_SUBPILAR_DATA_INICIO, DatabaseHelper.COLUMN_SUBPILAR_DATA_TERMINO, DatabaseHelper.COLUMN_SUBPILAR_ID_PILAR),
                        "${DatabaseHelper.COLUMN_SUBPILAR_ID} = ?",
                        arrayOf(idVinculoSelecionado.toString()),
                        null,
                        null,
                        null
                    )
                    cursorSubpilar?.use {
                        if (it.moveToFirst()) {
                            val dataInicioSubpilarStr = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_DATA_INICIO))
                            val dataTerminoSubpilarStr = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_DATA_TERMINO))
                            val idPilarPai = it.getLong(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_ID_PILAR))

                            val dataInicioSubpilar = sdf.parse(dataInicioSubpilarStr)
                            val dataTerminoSubpilar = sdf.parse(dataTerminoSubpilarStr)
                            val dataAcao = sdf.parse(dataAcaoStr)

                            if (dataAcao.before(dataInicioSubpilar)) {
                                Toast.makeText(this@CadastroAcaoActivity, "A data de início da Ação não pode ser anterior à data de início do Subpilar.", Toast.LENGTH_SHORT).show()
                                return null
                            }
                            if (dataAcao.after(dataTerminoSubpilar)) {
                                Toast.makeText(this@CadastroAcaoActivity, "A data de início da Ação não pode ser posterior à data de término do Subpilar.", Toast.LENGTH_SHORT).show()
                                return null
                            }
                            if (anoAcaoStr != dataInicioSubpilarStr.split("/")[2]) {
                                Toast.makeText(this@CadastroAcaoActivity, "O ano da data de início da Ação deve ser o mesmo do Subpilar.", Toast.LENGTH_SHORT).show()
                                return null
                            }

                            // Opcional: Você pode também buscar as datas do Pilar pai do Subpilar se precisar de validações adicionais em relação ao Pilar.
                        }
                    }
                    cursorSubpilar?.close()
                }
            } catch (e: ParseException) {
                Toast.makeText(this@CadastroAcaoActivity, "Erro ao comparar as datas.", Toast.LENGTH_SHORT).show()
                return null
            } finally {
                db.close()
            }
        } else {
            Toast.makeText(this@CadastroAcaoActivity, "Selecione um Pilar ou Subpilar para validar a data de início.", Toast.LENGTH_SHORT).show()
            return null
        }

        return try {
            val dia = diaAcaoStr.toInt()
            val mes = mesAcaoStr.toInt()
            val ano = anoAcaoStr.toInt()

            if (dia < 1 || dia > 31 || mes < 1 || mes > 12 || ano < 2025 || ano > 2100) {
                return null
            }

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.isLenient = false
            sdf.parse("$dia/$mes/$ano")
            dataAcaoStr
        } catch (e: NumberFormatException) {
            null
        } catch (e: ParseException) {
            null
        }
    }

    private fun validarEFormatarDataFinal(dataTerminoAcaoStr: String, dataInicioAcaoStr: String, tipoVinculo: String, idVinculoSelecionado: Int): String? {
        if (dataTerminoAcaoStr.isNullOrEmpty()) {
            return null
        }

        val partesTerminoAcao = dataTerminoAcaoStr.split("/")
        if (partesTerminoAcao.size != 3) {
            return null
        }

        val diaTerminoAcaoStr = partesTerminoAcao[0]
        val mesTerminoAcaoStr = partesTerminoAcao[1]
        val anoTerminoAcaoStr = partesTerminoAcao[2]

        if (diaTerminoAcaoStr.length != 2 || mesTerminoAcaoStr.length != 2 || anoTerminoAcaoStr.length != 4) {
            return null
        }

        val partesInicioAcao = dataInicioAcaoStr.split("/")
        if (partesInicioAcao.size != 3) {
            return null
        }

        val anoInicioAcaoStr = partesInicioAcao[2]
        if (anoTerminoAcaoStr != anoInicioAcaoStr) {
            Toast.makeText(this@CadastroAcaoActivity, "O ano da data de término deve ser o mesmo da data de início.", Toast.LENGTH_SHORT).show()
            return null
        }
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        sdf.isLenient = false

        if (idVinculoSelecionado != -1L.toInt() && tipoVinculo.isNotEmpty()) {
            val db = dbHelper.readableDatabase


            try {
                if (tipoVinculo == "pilar") {
                    val cursorPilar = db.query(
                        DatabaseHelper.TABLE_PILAR,
                        arrayOf(DatabaseHelper.COLUMN_PILAR_DATA_TERMINO),
                        "${DatabaseHelper.COLUMN_PILAR_ID} = ?",
                        arrayOf(idVinculoSelecionado.toString()),
                        null,
                        null,
                        null
                    )
                    cursorPilar?.use {
                        if (it.moveToFirst()) {
                            val dataTerminoPilarStr = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_DATA_TERMINO))
                            val dataTerminoPilar = sdf.parse(dataTerminoPilarStr)
                            val dataTerminoAcao = sdf.parse(dataTerminoAcaoStr)

                            if (dataTerminoAcao.after(dataTerminoPilar)) {
                                Toast.makeText(this@CadastroAcaoActivity, "A data de término da Ação não pode ser posterior à data de término do Pilar.", Toast.LENGTH_SHORT).show()
                                return null
                            }
                        }
                    }
                    cursorPilar?.close()
                } else if (tipoVinculo == "subpilar") {
                    val cursorSubpilar = db.query(
                        DatabaseHelper.TABLE_SUBPILAR,
                        arrayOf(DatabaseHelper.COLUMN_SUBPILAR_DATA_TERMINO),
                        "${DatabaseHelper.COLUMN_SUBPILAR_ID} = ?",
                        arrayOf(idVinculoSelecionado.toString()),
                        null,
                        null,
                        null
                    )
                    cursorSubpilar?.use {
                        if (it.moveToFirst()) {
                            val dataTerminoSubpilarStr = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_DATA_TERMINO))
                            val dataTerminoSubpilar = sdf.parse(dataTerminoSubpilarStr)
                            val dataTerminoAcao = sdf.parse(dataTerminoAcaoStr)

                            if (dataTerminoAcao.after(dataTerminoSubpilar)) {
                                Toast.makeText(this@CadastroAcaoActivity, "A data de término da Ação não pode ser posterior à data de término do Subpilar.", Toast.LENGTH_SHORT).show()
                                return null
                            }
                        }
                    }
                    cursorSubpilar?.close()
                }
            } catch (e: ParseException) {
                Toast.makeText(this@CadastroAcaoActivity, "Erro ao comparar as datas de término.", Toast.LENGTH_SHORT).show()
                return null
            } finally {
                db.close()
            }
        } else {
            Toast.makeText(this@CadastroAcaoActivity, "Selecione um Pilar ou Subpilar para validar a data de término.", Toast.LENGTH_SHORT).show()
            return null
        }

        return try {
            val diaTermino = diaTerminoAcaoStr.toInt()
            val mesTermino = mesTerminoAcaoStr.toInt()
            val anoTermino = anoTerminoAcaoStr.toInt()

            val diaInicio = partesInicioAcao[0].toInt()
            val mesInicio = partesInicioAcao[1].toInt()
            val anoInicio = anoInicioAcaoStr.toInt()

            val calendarInicio = Calendar.getInstance()
            calendarInicio.set(anoInicio, mesInicio - 1, diaInicio)
            val dataInicioDate = calendarInicio.time

            val calendarTermino = Calendar.getInstance()
            calendarTermino.set(anoTermino, mesTermino - 1, diaTermino)
            val dataTerminoDate = calendarTermino.time

            if (dataTerminoDate.before(dataInicioDate)) {
                Toast.makeText(this@CadastroAcaoActivity, "A data de término não pode ser anterior à data de início.", Toast.LENGTH_SHORT).show()
                return null
            }

            sdf.parse("$diaTermino/$mesTermino/$anoTermino")
            dataTerminoAcaoStr
        } catch (e: NumberFormatException) {
            null
        } catch (e: ParseException) {
            null
        }
    }
}
