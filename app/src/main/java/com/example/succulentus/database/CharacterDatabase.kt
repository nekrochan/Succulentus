package com.example.succulentus.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.succulentus.data.Character

@Database(entities = [Character::class], version = 1)
abstract class CharacterDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
}