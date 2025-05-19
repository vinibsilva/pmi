package com.example.mpi.ui.subpilar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mpi.databinding.SubpilarItemFragmentBinding
import com.example.mpi.data.Subpilar

class SubpilarItemFragment(
    private val subpilar: Subpilar,
    private val onEditarClicked: (Subpilar) -> Unit,
    private val onExcluirClicked: (Subpilar) -> Unit
) : Fragment() {

    private var _binding: SubpilarItemFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SubpilarItemFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvNomeSubpilarItem.text = subpilar.nome
        binding.tvDescricaoSubpilarItem.text = subpilar.descricao
        binding.tvDataInicioSubpilarItem.text = "Início: ${subpilar.dataInicio}"
        binding.tvDataTerminoSubpilarItem.text = "Término: ${subpilar.dataTermino}"
        binding.tvAprovadoSubpilarItem.text = if (subpilar.aprovado) "Aprovado" else "Pendente"
        binding.tvIdPilarSubpilarItem.text = "ID Pilar associado: ${subpilar.idPilar}"

        binding.btnEditarSubpilar.setOnClickListener {
            onEditarClicked(subpilar)
        }

        binding.btnExcluirSubpilar.setOnClickListener {
            onExcluirClicked(subpilar)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}