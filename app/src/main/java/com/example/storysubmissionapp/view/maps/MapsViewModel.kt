package com.example.storysubmissionapp.view.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storysubmissionapp.data.StoryRepository
import com.example.storysubmissionapp.data.model.UserModel

class MapsViewModel(private val mStoryRepository: StoryRepository) : ViewModel() {
    fun getSessionData(): LiveData<UserModel> =
        mStoryRepository.getSession().asLiveData()

    fun getStories(token: String) =
        mStoryRepository.getStories(token)
}