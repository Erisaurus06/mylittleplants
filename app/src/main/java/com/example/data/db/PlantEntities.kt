package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_plants")
data class UserPlant(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val scientificName: String,
    val category: String, // "Edible", "Poisonous", "Medicinal", "Tree"
    val customNotes: String,
    val feedingIntervalDays: Int = 3, // Watering/feeding frequency in days
    val lastFedTimestamp: Long = 0L,  // 0 means never updated
    val addedTimestamp: Long = System.currentTimeMillis()
)
