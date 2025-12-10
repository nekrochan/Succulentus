package com.example.succulentus.data

import kotlinx.serialization.Serializable

@Serializable
data class Character(  // вместо ApiResponse
    val _id: Integer? = null,
    val url: String? = null,
    val name: String? = null,
    val imageUrl: String? = null,
    val films: List<String>? = null,
    val shortFilms: List<String>? = null,
    val tvShows: List<String>? = null,
    val videoGames: List<String>? = null,
    val alignment: String? = null,
    val parkAttractions: List<String>? = null,
    val allies: List<String>? = null,
    val enemies: List<String>? = null
)