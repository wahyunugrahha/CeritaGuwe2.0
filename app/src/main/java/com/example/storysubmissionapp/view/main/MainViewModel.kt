package com.example.storysubmissionapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storysubmissionapp.data.local.repository.StoryRepository
import com.example.storysubmissionapp.data.model.Story
import com.example.storysubmissionapp.data.model.UserModel
import kotlinx.coroutines.launch

class MainViewModel(private val mStoryRepository: StoryRepository) : ViewModel() {
    fun getSessionData(): LiveData<UserModel> =
        mStoryRepository.getSession().asLiveData()


    fun stories(token: String): LiveData<PagingData<Story>> =
        mStoryRepository.getStories(token).cachedIn(viewModelScope)

    fun logout() {
        viewModelScope.launch {
            mStoryRepository.logout()
        }
    }
}