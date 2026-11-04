package com.pokedex.app.viewmodel

import com.pokedex.app.data.model.PokemonDto

data class PokedexUiState(
    val consulta: String = "pikachu",
    val cargando: Boolean = false,
    val pokemon: PokemonDto? = null,
    val error: String? = null
)
