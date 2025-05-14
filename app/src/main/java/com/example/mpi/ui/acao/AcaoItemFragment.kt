package com.example.mpi.ui.acao

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mpi.data.Acao
import com.example.mpi.databinding.AcaoItemFragmentBinding

class AcaoItemFragment(
    private val acao: Acao,
    private val onEditarClicked: (Acao) -> Unit,
    private val onExcluirClicked: (Acao) -> Unit
) : Fragment() {

    private var _binding: AcaoItemFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AcaoItemFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvNomeAcaoItem.text = acao.nome
        binding.tvDescricaoAcaoItem.text = acao.descricao
        binding.tvDataInicioAcaoItem.text = "Início: ${acao.dataInicio}"
        binding.tvDataTerminoAcaoItem.text = "Término: ${acao.dataTermino}"
        binding.tvResponsavelAcaoItem.text = "Responsável: ${acao.responsavel}"
        binding.tvAprovadoAcaoItem.text = if (acao.aprovado) "Aprovada" else "Não Aprovada"
        binding.tvFinalizadaAcaoItem.text = if (acao.finalizado) "Finalizada" else "Não Finalizada"

        binding.btnEditarAcao.setOnClickListener {
            onEditarClicked(acao)
        }

        binding.btnExcluirAcao.setOnClickListener {
            onExcluirClicked(acao)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}