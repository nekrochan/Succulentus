package com.example.succulentus

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.succulentus.data.Character
import com.example.succulentus.databinding.CharacterBinding

class CharacterAdapter(private val characters: List<Character>?) : RecyclerView.Adapter<CharacterAdapter.HomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = CharacterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        characters?.let { holder.bind(it[position]) }
    }

    override fun getItemCount(): Int = characters!!.size

    class HomeViewHolder(private val binding: CharacterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(character: Character) {
            binding.characterName.text = character.name
            binding.films.text = character.films?.joinToString(", ") ?: "No films"
            if (binding.films.text.length==0) binding.films.text = "No films"
        }
    }
}