package com.example.mpi.ui.pilar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mpi.data.Pilar
import com.example.mpi.databinding.FragmentPilarItemBinding

class PilarAdapter(
    private val listaPilares: List<Pilar>,
    private val onEditarClicked: (Pilar) -> Unit,
    private val onExcluirClicked: (Pilar) -> Unit
) : RecyclerView.Adapter<PilarAdapter.PilarViewHolder>() {

    inner class PilarViewHolder(binding: FragmentPilarItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvNome = binding.tvNomePilarItem
        val tvDescricao = binding.tvDescricaoPilarItem
        val tvDataInicio = binding.tvDataInicioPilarItem
        val tvDataTermino = binding.tvDataTerminoPilarItem
        val tvPercentual = binding.tvPercentualPilarItem
        val tvAprovado = binding.tvAprovadoPilarItem
        val btnEditar = binding.btnEditarPilar
        val btnExcluir = binding.btnExcluirPilar
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PilarViewHolder {
        val binding = FragmentPilarItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PilarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PilarViewHolder, position: Int) {
        val pilar = listaPilares[position]
        holder.tvNome.text = pilar.nome
        holder.tvDescricao.text = pilar.descricao
        holder.tvDataInicio.text = "Início: ${pilar.dataInicio}"
        holder.tvDataTermino.text = "Término: ${pilar.dataTermino}"
        holder.tvPercentual.text = String.format("%.2f%%", pilar.percentual * 100)
        holder.tvAprovado.text = if (pilar.aprovado) "Aprovado" else "Pendente"

        holder.btnEditar.setOnClickListener {
            onEditarClicked(pilar)
        }

        holder.btnExcluir.setOnClickListener {
            onExcluirClicked(pilar)
        }
    }

    override fun getItemCount(): Int = listaPilares.size
}