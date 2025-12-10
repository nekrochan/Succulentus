package com.rut.roomexample.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.succulentus.data.Character
import com.example.succulentus.database.CharacterDao

@Database(entities = [Character::class], version = 1)
abstract class CharacterDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
}