package com.example.storysubmissionapp.di

import android.content.Context
import com.example.storysubmissionapp.data.local.database.StoryDatabase
import com.example.storysubmissionapp.data.local.pref.UserPreferences
import com.example.storysubmissionapp.data.local.pref.dataStore
import com.example.storysubmissionapp.data.local.repository.StoryRepository
import com.example.storysubmissionapp.data.remote.retrofit.ApiConfig


object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val pref = UserPreferences.getInstance(context.dataStore)
        val database = StoryDatabase.getDatabase(context)
        return StoryRepository.getInstance(apiService, pref, database)
    }
}