package com.example.storysubmissionapp.data.remote.response

import com.example.storysubmissionapp.data.model.Story


data class StoryResponse(
    val error: Boolean,
    val message: String,
    val story: Story
)

