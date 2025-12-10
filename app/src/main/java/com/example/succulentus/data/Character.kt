package com.example.succulentus.data

import kotlinx.serialization.Serializable

@Serializable
data class Character(  // вместо ApiResponse
    val url: String? = null,
    val name: String? = null,
    val gender: String? = null,
    val culture: String? = null,
    val born: String? = null,
    val died: String? = null,
    val titles: List<String>? = null,
    val aliases: List<String>? = null,
    val father: String? = null,
    val mother: String? = null,
    val spouse: String? = null,
    val allegiances: List<String>? = null,
    val books: List<String>? = null,
    val povBooks: List<String>? = null,
    val tvSeries: List<String>? = null,
    val playedBy: List<String>? = null
)