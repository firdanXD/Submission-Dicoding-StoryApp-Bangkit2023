package com.firdan.storyapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.firdan.storyapp.data.repository.StoryRepository
import com.firdan.storyapp.data.repository.UserRepository


class StoryMapViewModel(
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    fun getStories(token: String) = storyRepository.getStoriesWithLocation(token, page = 1, size = 10)


    fun getToken(): LiveData<String> {
        return userRepository.getAuthToken()
    }


}