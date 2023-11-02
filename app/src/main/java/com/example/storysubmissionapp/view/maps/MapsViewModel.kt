package com.example.storysubmissionapp.view.maps

import androidx.lifecycle.ViewModel
import com.example.storysubmissionapp.data.StoryRepository

class MapsViewModel(private val repository: StoryRepository) : ViewModel() {
    fun stories() = repository.getStories(token)
}