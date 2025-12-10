package com.example.succulentus.database


import androidx.room.TypeConverter

object TypeConverter {
    @TypeConverter
    fun fromEpochMillis(list: List<String>): String {
        return list.joinToString { ", " }
    }

    @TypeConverter
    fun toEpochMillies(data: String): List<String> {
        return listOf(*data.split(", ").toTypedArray())
    }

}