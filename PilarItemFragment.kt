package com.example.mpi.ui.pilar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mpi.databinding.FragmentPilarItemBinding
import com.example.mpi.data.Pilar

class PilarItemFragment(
    private val pilar: Pilar,
    private val onEditarClicked: (Pilar) -> Unit,
    private val onExcluirClicked: (Pilar) -> Unit
) : Fragment() {

    private var _binding: FragmentPilarItemBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPilarItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvNomePilarItem.text = pilar.nome
        binding.tvDescricaoPilarItem.text = pilar.descricao
        binding.tvDataInicioPilarItem.text = "Início: ${pilar.dataInicio}"
        binding.tvDataTerminoPilarItem.text = "Término: ${pilar.dataTermino}"
        binding.tvPercentualPilarItem.text = "Percentual: ${String.format("%.2f%%", pilar.percentual * 100)}"
        binding.tvAprovadoPilarItem.text = if (pilar.aprovado) "Aprovado" else "Pendente"
        binding.tvIdUsuarioPilarItem.text = "ID Usuário: ${pilar.idUsuario}"

        binding.btnEditarPilar.setOnClickListener {
            onEditarClicked(pilar)
        }

        binding.btnExcluirPilar.setOnClickListener {
            onExcluirClicked(pilar)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}