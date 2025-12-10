package com.example.succulentus.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class Character(  // вместо ApiResponse
    @PrimaryKey val _id: Integer? = null,
    @ColumnInfo(name = "url") val url: String? = null,
    @ColumnInfo(name = "name") val name: String? = null,
    @ColumnInfo(name = "imageUrl") val imageUrl: String? = null,
    @ColumnInfo(name = "films") val films: List<String>? = null,
    @ColumnInfo(name = "shortFilms") val shortFilms: List<String>? = null,
    @ColumnInfo(name = "tvShows") val tvShows: List<String>? = null,
    @ColumnInfo(name = "videoGames") val videoGames: List<String>? = null,
    @ColumnInfo(name = "alignment") val alignment: String? = null,
    @ColumnInfo(name = "parkAttractions") val parkAttractions: List<String>? = null,
    @ColumnInfo(name = "allies") val allies: List<String>? = null,
    @ColumnInfo(name = "enemies") val enemies: List<String>? = null
)