package com.example.mpi.ui.pilar

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mpi.R
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.databinding.ActivityCadastroPilarBinding
import com.example.mpi.repository.CalendarioRepository
import java.util.Calendar
import java.lang.NumberFormatException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class cadastroPilar : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroPilarBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var calendarioRepository: CalendarioRepository
    private var idUsuarioRecebido: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCadastroPilarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        calendarioRepository = CalendarioRepository.getInstance(this)

        // Recebendo os dados de usuário
        val extras = intent.extras
        if (extras != null) {
            idUsuarioRecebido = extras.getInt("idUsuario", 999999)
            val nomeUsuario = extras.getString("nomeUsuario") ?: "Nome de usuário desconhecido"
            val tipoUsuario = extras.getString("tipoUsuario") ?: "Tipo de usuário desconhecido"
        }


        binding.btnconfirmarCadastro.setOnClickListener {
            val nome = binding.etnomePilar.text.toString()
            val descricao = binding.etdescricaoPilar.text.toString()
            val dataInicio = binding.etdataInicio.text.toString().trim()
            val dataTermino = binding.etdataTermino.text.toString().trim()
            if (nome.isEmpty() || descricao.isEmpty() || dataInicio.isEmpty() || dataTermino.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validação das datas informadas
            val dataInicioFormatada = validarEFormatarDataInicial(dataInicio)
            val dataTerminoFormatada = validarEFormatarDataFinal(dataTermino, dataInicio)

            if (dataInicioFormatada == null) {
                Toast.makeText(this, "Data de Início inválida. Use o formato dd/mm/aaaa.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (dataTerminoFormatada == null) {
                Toast.makeText(this, "Data de Término inválida. Use o formato dd/mm/aaaa.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Extraindo o ano informado e validado para adicionar um novo registro de calendário, em caso de ser o primeiro pilar
            val partesDataInicioFormatada = dataInicio.split("/") //
            val anoCalendarioStr = partesDataInicioFormatada.getOrNull(2)
            if (anoCalendarioStr.isNullOrEmpty()) {
                Toast.makeText(this, "Formato da data de início inválido. Use dd/mm/aaaa", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val anoCalendario = anoCalendarioStr.toIntOrNull()
            if (anoCalendario == null) {
                Toast.makeText(this, "Ano da data de início inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var idCalendario: Int

            if (calendarioRepository.contarPilares() == 0) {
                // Não existe nenhum pilar, então criamos um registro de calendário para o ano da data de início
                val calendarioIdExistente = calendarioRepository.obterIdCalendarioPorAno(anoCalendario)
                if (calendarioIdExistente == (-1L).toInt()) {
                    idCalendario = calendarioRepository.inserirCalendario(anoCalendario)
                    if (idCalendario == (-1L).toInt()) {
                        Toast.makeText(this, "Erro ao criar registro de calendário inicial", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                } else {
                    idCalendario = calendarioIdExistente
                }
            } else {
                // Já tinha pilares, então o primeiro ID de calendário será consultado e implementado na tabela Pilar
                val db = dbHelper.readableDatabase
                val cursor = db.rawQuery("SELECT ${DatabaseHelper.COLUMN_PILAR_ID_CALENDARIO} FROM ${DatabaseHelper.TABLE_PILAR} LIMIT 1", null)
                if (cursor != null && cursor.moveToFirst()) {
                    idCalendario = cursor.getInt(0)
                    cursor.close()
                } else {
                    Toast.makeText(this, "Erro ao obter ID do calendário existente", Toast.LENGTH_SHORT).show()
                    db.close()
                    return@setOnClickListener
                }
                db.close()

            }


            val db = dbHelper.writableDatabase
            val values = android.content.ContentValues().apply {
                put(DatabaseHelper.COLUMN_PILAR_NOME, nome)
                put(DatabaseHelper.COLUMN_PILAR_DESCRICAO, descricao)
                put(DatabaseHelper.COLUMN_PILAR_DATA_INICIO, dataInicioFormatada)
                put(DatabaseHelper.COLUMN_PILAR_DATA_TERMINO, dataTerminoFormatada)
                put(DatabaseHelper.COLUMN_PILAR_PERCENTUAL, 0.0)
                put(DatabaseHelper.COLUMN_PILAR_IS_APROVADO, 0)
                put(DatabaseHelper.COLUMN_PILAR_ID_CALENDARIO, idCalendario)
                put(DatabaseHelper.COLUMN_PILAR_ID_USUARIO, idUsuarioRecebido)
            }

            val newRowId = db.insert(DatabaseHelper.TABLE_PILAR, null, values)
            db.close()

            if (newRowId == -1L) {
                Toast.makeText(this, "Erro ao cadastrar o Pilar.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Pilar cadastrado com sucesso! Atualize a página de listagem.", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }
        }

        binding.btnVoltar.setOnClickListener {
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Validação da data(valida os valores inseridos e retorna a data com o tipo Text
    private fun validarEFormatarDataInicial(data: String): String? {
        if (data.isNullOrEmpty()) {
            return null
        }

        val partes = data.split("/")
        if (partes.size != 3) {
            return null
        }

        val diaStr = partes[0]
        val mesStr = partes[1]
        val anoStr = partes[2]

        if (diaStr.length != 2 || mesStr.length != 2 || anoStr.length != 4) {
            return null
        }

        return try {
            val dia = diaStr.toInt()
            val mes = mesStr.toInt()
            val ano = anoStr.toInt()

            if (dia < 1 || dia > 31 || mes < 1 || mes > 12 || ano < 2025 || ano > 2100) {
                return null
            }

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.isLenient = false
            sdf.parse("$dia/$mes/$ano")
            data
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

        if (anoTerminoStr != anoInicioStr) {
            Toast.makeText(this@cadastroPilar, "O ano da data de término deve ser o mesmo da data de início.", Toast.LENGTH_SHORT).show()
            return null
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
                Toast.makeText(this@cadastroPilar, "A data de término não pode ser anterior à data de início.", Toast.LENGTH_SHORT).show()
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



