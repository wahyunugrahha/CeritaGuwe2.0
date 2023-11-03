package com.example.storysubmissionapp

import com.example.storysubmissionapp.data.model.Story


import java.time.LocalDate

object DataDummy {
    fun generateDummyStories(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val story = Story(
                i.toString(),
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                LocalDate.now().toString(),
                "Testing Name",
                "Lorem Ipsum Desc",
                1.0,
                1.0,
            )
            items.add(story)
        }
        return items
    }
}