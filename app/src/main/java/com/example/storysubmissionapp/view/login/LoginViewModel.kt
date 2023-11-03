package com.example.storysubmissionapp.view.login

import androidx.lifecycle.ViewModel
import com.example.storysubmissionapp.data.local.repository.StoryRepository

class LoginViewModel(
    private val mStoryRepository: StoryRepository
) : ViewModel() {

    fun login(email: String, password: String) =
        mStoryRepository.loginUser(email, password)
}