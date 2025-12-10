package com.example.succulentus.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.succulentus.data.Character

@Dao
interface CharacterDao {
    @Query("SELECT * FROM Character")
    fun getAllLiveData(): LiveData<List<Character>>

    @Query("SELECT * FROM Character")
    fun getAll(): List<Character>

    @Query("SELECT * FROM Character WHERE _id = :id")
    suspend fun getCharacterById(id: Int): Character?

    @Query("SELECT * FROM Character WHERE name LIKE '%' || :query || '%'")
    suspend fun searchCharacters(query: String): List<Character>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(character: Character): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(characters: List<Character>)

    @Delete
    suspend fun delete(character: Character)

    @Update
    suspend fun update(character: Character)

    @Query("DELETE FROM Character")
    suspend fun deleteAll()
}