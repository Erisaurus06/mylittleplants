package com.example.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    @Query("SELECT * FROM user_plants ORDER BY addedTimestamp DESC")
    fun getAllUserPlants(): Flow<List<UserPlant>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserPlant(plant: UserPlant): Long

    @Update
    suspend fun updateUserPlant(plant: UserPlant)

    @Query("DELETE FROM user_plants WHERE id = :id")
    suspend fun deleteUserPlantById(id: Int)

    @Query("UPDATE user_plants SET lastFedTimestamp = :timestamp WHERE id = :id")
    suspend fun feedPlant(id: Int, timestamp: Long)
}
