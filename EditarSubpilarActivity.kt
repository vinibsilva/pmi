package com.example.mpi.ui.subpilar

import android.content.ContentValues
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.databinding.ActivityEditarSubpilarBinding
import com.example.mpi.data.Pilar
import java.lang.NumberFormatException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditarSubpilarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarSubpilarBinding
    private lateinit var dbHelper: DatabaseHelper
    private var subpilarId: Int = -1
    private var idPilarAtual: Int = -1
    private var listaPilaresNomesEditar = mutableListOf<String>()
    private var listaPilaresObjetosEditar = mutableListOf<Pilar>()
    private var novoIdPilarSelecionado: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarSubpilarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)


        val extras = intent.extras
        if (extras != null) {
            subpilarId = extras.getInt("subpilar_id", -1)
            val nome = extras.getString("subpilar_nome")
            val descricao = extras.getString("subpilar_descricao")
            val dataInicio = extras.getString("subpilar_data_inicio")
            val dataTermino = extras.getString("subpilar_data_termino")
            val aprovado = extras.getBoolean("subpilar_aprovado")
            idPilarAtual = extras.getInt("subpilar_id_pilar", -1)


            binding.etEditarNomeSubpilar.setText(nome)
            binding.etEditarDescricaoSubpilar.setText(descricao)
            binding.etEditarDataInicio.setText(dataInicio)
            binding.etEditarDataTermino.setText(dataTermino)
            binding.tvExibirAprovado.text = if (aprovado) "Sim" else "Não"

            carregarPilaresNoSpinnerEditar(idPilarAtual)
        }

        binding.spinnerPilaresEditar.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position > 0) {
                        novoIdPilarSelecionado = listaPilaresObjetosEditar[position - 1].id
                    } else {
                        novoIdPilarSelecionado = -1
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    novoIdPilarSelecionado =
                        idPilarAtual // Mantém o pilar atual se nada for selecionado
                }
            }

        binding.btnSalvarEdicao.setOnClickListener {
            salvarEdicaoSubpilar()
        }

        binding.btnVoltarEditar.setOnClickListener {
            finish()
        }
    }

    private fun carregarPilaresNoSpinnerEditar(idPilarSelecionado: Int) {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(DatabaseHelper.COLUMN_PILAR_ID, DatabaseHelper.COLUMN_PILAR_NOME)
        val cursor = db.query(
            DatabaseHelper.TABLE_PILAR,
            projection,
            null,
            null,
            null,
            null,
            DatabaseHelper.COLUMN_PILAR_NOME
        )

        listaPilaresNomesEditar.clear()
        listaPilaresObjetosEditar.clear()
        listaPilaresNomesEditar.add("Selecione o Pilar Pai") // Hint

        var pilarSelecionadoPosition = 0

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_ID))
                val nome = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_NOME))
                listaPilaresObjetosEditar.add(Pilar(id, nome, "", "", "", false, 0.0, 0, 0))
                listaPilaresNomesEditar.add(nome)
                if (id == idPilarSelecionado) {
                    pilarSelecionadoPosition = listaPilaresNomesEditar.size - 1
                }
            }
        }
        cursor.close()
        db.close()

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listaPilaresNomesEditar
        )
        binding.spinnerPilaresEditar.adapter = adapter
        binding.spinnerPilaresEditar.setSelection(pilarSelecionadoPosition)
        novoIdPilarSelecionado = idPilarSelecionado // Inicializa com o pilar atual
    }

    private fun salvarEdicaoSubpilar() {
        val nome = binding.etEditarNomeSubpilar.text.toString()
        val descricao = binding.etEditarDescricaoSubpilar.text.toString()
        val dataInicio = binding.etEditarDataInicio.text.toString()
        val dataTermino = binding.etEditarDataTermino.text.toString()

        if (nome.isEmpty() || descricao.isEmpty() || dataInicio.isEmpty() || dataTermino.isEmpty() || novoIdPilarSelecionado == -1L.toInt()) {
            Toast.makeText(
                this,
                "Preencha todos os campos e selecione um Pilar Pai",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Validação das datas informadas
        val dataInicioFormatada = validarEFormatarDataInicial(dataInicio)
        val dataTerminoFormatada = validarEFormatarDataFinal(dataTermino, dataInicio)

        if (dataInicioFormatada == null) {
            Toast.makeText(
                this,
                "Data de Início inválida. Use o formato dd/mm/aaaa.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (dataTerminoFormatada == null) {
            Toast.makeText(
                this,
                "Data de Término inválida. Use o formato dd/mm/aaaa.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }


        if (subpilarId != -1L.toInt()) {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put(DatabaseHelper.COLUMN_SUBPILAR_NOME, nome)
                put(DatabaseHelper.COLUMN_SUBPILAR_DESCRICAO, descricao)
                put(DatabaseHelper.COLUMN_SUBPILAR_DATA_INICIO, dataInicio)
                put(DatabaseHelper.COLUMN_SUBPILAR_DATA_TERMINO, dataTermino)
                put(DatabaseHelper.COLUMN_SUBPILAR_ID_PILAR, novoIdPilarSelecionado)

            }

            val whereClause = "${DatabaseHelper.COLUMN_SUBPILAR_ID} = ?"
            val whereArgs = arrayOf(subpilarId.toString())
            val rowsAffected = db.update(
                DatabaseHelper.TABLE_SUBPILAR,
                values,
                whereClause,
                whereArgs
            )

            db.close()

            if (rowsAffected > 0) {
                Toast.makeText(this, "Subpilar atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Erro ao atualizar o subpilar.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Erro: ID do subpilar não encontrado.", Toast.LENGTH_SHORT).show()
        }
    }

    // Validação da data(valida os valores inseridos e retorna a data com o tipo Text
    private fun validarEFormatarDataInicial(dataSubpilarStr: String): String? {
        if (dataSubpilarStr.isNullOrEmpty()) {
            return null
        }

        val partesSubpilar = dataSubpilarStr.split("/")
        if (partesSubpilar.size != 3) {
            return null
        }

        val diaSubpilarStr = partesSubpilar[0]
        val mesSubpilarStr = partesSubpilar[1]
        val anoSubpilarStr = partesSubpilar[2]

        if (diaSubpilarStr.length != 2 || mesSubpilarStr.length != 2 || anoSubpilarStr.length != 4) {
            return null
        }

        if (novoIdPilarSelecionado != -1L.toInt()) {
            val db = dbHelper.readableDatabase
            val cursorPilar = db.query(
                DatabaseHelper.TABLE_PILAR,
                arrayOf(DatabaseHelper.COLUMN_PILAR_DATA_INICIO, DatabaseHelper.COLUMN_PILAR_DATA_TERMINO),
                "${DatabaseHelper.COLUMN_PILAR_ID} = ?",
                arrayOf(novoIdPilarSelecionado.toString()),
                null,
                null,
                null
            )

            cursorPilar?.use {
                if (it.moveToFirst()) {
                    val dataInicioPilarStr = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_DATA_INICIO))
                    val dataTerminoPilarStr = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_DATA_TERMINO))

                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    sdf.isLenient = false

                    try {
                        val dataInicioPilar = sdf.parse(dataInicioPilarStr)
                        val dataTerminoPilar = sdf.parse(dataTerminoPilarStr)
                        val dataSubpilar = sdf.parse(dataSubpilarStr)

                        if (dataSubpilar.before(dataInicioPilar)) {
                            Toast.makeText(
                                this@EditarSubpilarActivity,
                                "A data de início do Subpilar não pode ser anterior à data de início do Pilar.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return null
                        }

                        if (dataSubpilar.after(dataTerminoPilar)) {
                            Toast.makeText(
                                this@EditarSubpilarActivity,
                                "A data de início do Subpilar não pode ser posterior à data de término do Pilar.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return null
                        }

                        val partesPilarInicio = dataInicioPilarStr.split("/")
                        val anoPilarInicioStr = partesPilarInicio.getOrNull(2)

                        if (anoPilarInicioStr != anoSubpilarStr) {
                            Toast.makeText(
                                this@EditarSubpilarActivity,
                                "O ano da data de início do Subpilar deve ser o mesmo do Pilar Pai ($anoPilarInicioStr).",
                                Toast.LENGTH_SHORT
                            ).show()
                            return null
                        }

                    } catch (e: ParseException) {
                        Toast.makeText(this@EditarSubpilarActivity, "Erro ao comparar as datas.", Toast.LENGTH_SHORT).show()
                        return null
                    }
                }
            }
            cursorPilar?.close()
            db.close()
        } else {
            Toast.makeText(this@EditarSubpilarActivity, "Selecione um Pilar Pai para validar a data de início.", Toast.LENGTH_SHORT).show()
            return null // Não pode validar sem um pilar selecionado
        }

        return try {
            val dia = diaSubpilarStr.toInt()
            val mes = mesSubpilarStr.toInt()
            val ano = anoSubpilarStr.toInt()

            if (dia < 1 || dia > 31 || mes < 1 || mes > 12 || ano < 2025 || ano > 2100) {
                return null
            }

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.isLenient = false
            sdf.parse("$dia/$mes/$ano")
            dataSubpilarStr
        } catch (e: NumberFormatException) {
            null // Algum valor não é um número
        } catch (e: ParseException) {
            null // Data inválida
        }
    }

    private fun validarEFormatarDataFinal(dataTerminoStr: String, dataInicioStr: String): String? {
        if (dataTerminoStr.isNullOrEmpty()) {
            return null
        }

        val partesTermino = dataTerminoStr.split("/")
        if (partesTermino.size != 3) {
            return null
        }

        val diaTerminoStr = partesTermino[0]
        val mesTerminoStr = partesTermino[1]
        val anoTerminoStr = partesTermino[2]

        if (diaTerminoStr.length != 2 || mesTerminoStr.length != 2 || anoTerminoStr.length != 4) {
            return null
        }


        val partesInicio = dataInicioStr.split("/")
        if (partesInicio.size != 3) {
            return null
        }

        val anoInicioStr = partesInicio[2]
        // Validação do ano
        if (anoTerminoStr != anoInicioStr) {
            Toast.makeText(
                this@EditarSubpilarActivity,
                "O ano da data de término deve ser o mesmo da data de início.",
                Toast.LENGTH_SHORT
            ).show()
            return null
        }


        if (novoIdPilarSelecionado != -1L.toInt()) {
            val db = dbHelper.readableDatabase
            val cursorPilar = db.query(
                DatabaseHelper.TABLE_PILAR,
                arrayOf(DatabaseHelper.COLUMN_PILAR_DATA_TERMINO),
                "${DatabaseHelper.COLUMN_PILAR_ID} = ?",
                arrayOf(novoIdPilarSelecionado.toString()),
                null,
                null,
                null
            )

            cursorPilar?.use {
                if (it.moveToFirst()) {
                    val dataTerminoPilarStr =
                        it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_DATA_TERMINO))

                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    sdf.isLenient = false

                    try {
                        val dataTerminoPilar = sdf.parse(dataTerminoPilarStr)
                        val dataTerminoSubpilar = sdf.parse(dataTerminoStr)

                        if (dataTerminoSubpilar.after(dataTerminoPilar)) {
                            Toast.makeText(
                                this@EditarSubpilarActivity,
                                "A data de término do Subpilar não pode ser posterior à data de término do Pilar.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return null
                        }

                    } catch (e: ParseException) {
                        Toast.makeText(
                            this@EditarSubpilarActivity,
                            "Erro ao comparar as datas de término.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return null
                    }
                }
            }
            cursorPilar?.close()
            db.close()
        } else {
            Toast.makeText(
                this@EditarSubpilarActivity,
                "Selecione um Pilar Pai para validar a data de término.",
                Toast.LENGTH_SHORT
            ).show()
            return null // Não pode validar sem um pilar selecionado
        }


        return try {
            val diaTermino = diaTerminoStr.toInt()
            val mesTermino = mesTerminoStr.toInt()
            val anoTermino = anoTerminoStr.toInt()

            val diaInicio = partesInicio[0].toInt()
            val mesInicio = partesInicio[1].toInt()
            val anoInicio = anoInicioStr.toInt()

            val calendarInicio = Calendar.getInstance()
            calendarInicio.set(anoInicio, mesInicio - 1, diaInicio) // Mês em Calendar é de 0 a 11
            val dataInicioDate = calendarInicio.time

            val calendarTermino = Calendar.getInstance()
            calendarTermino.set(anoTermino, mesTermino - 1, diaTermino)
            val dataTerminoDate = calendarTermino.time

            if (dataTerminoDate.before(dataInicioDate)) {
                Toast.makeText(
                    this@EditarSubpilarActivity,
                    "A data de término não pode ser anterior à data de início.",
                    Toast.LENGTH_SHORT
                ).show()
                return null
            }

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.isLenient = false
            sdf.parse("$diaTermino/$mesTermino/$anoTermino")
            dataTerminoStr
        } catch (e: NumberFormatException) {
            null // Algum valor não é um número
        } catch (e: ParseException) {
            null // Data inválida
        }
    }
}

