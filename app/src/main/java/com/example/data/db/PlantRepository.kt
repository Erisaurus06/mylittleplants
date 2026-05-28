package com.example.data.db

import kotlinx.coroutines.flow.Flow

class PlantRepository(private val plantDao: PlantDao) {
    val allUserPlants: Flow<List<UserPlant>> = plantDao.getAllUserPlants()

    suspend fun insert(plant: UserPlant): Long {
        return plantDao.insertUserPlant(plant)
    }

    suspend fun update(plant: UserPlant) {
        plantDao.updateUserPlant(plant)
    }

    suspend fun deleteById(id: Int) {
        plantDao.deleteUserPlantById(id)
    }

    suspend fun feedPlant(id: Int, timestamp: Long = System.currentTimeMillis()) {
        plantDao.feedPlant(id, timestamp)
    }
}
