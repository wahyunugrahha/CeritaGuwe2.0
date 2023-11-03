package com.example.storysubmissionapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "story")
data class Story(
    val photoUrl: String,
    val createdAt: String,
    val name: String,
    val description: String,
    val lon: Double,
    @PrimaryKey
    val id: String,
    val lat: Double
)
