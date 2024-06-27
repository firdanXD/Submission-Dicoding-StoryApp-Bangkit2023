package com.firdan.storyapp.ui.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import com.firdan.storyapp.data.local.pref.StoryPref
import com.firdan.storyapp.data.repository.StoryRepository
import com.firdan.storyapp.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository,
    private val loginPreferences: StoryPref
) : ViewModel() {

    @ExperimentalPagingApi
    fun getStories(token: String) = storyRepository.getStories(token)

    fun getToken(): LiveData<String> {
        return userRepository.getAuthToken()
    }

    fun login(email: String, password: String) = userRepository.login(email, password)

    fun saveToken(token: String) {
        userRepository.saveAuthToken(token)
    }

    fun checkIfFirstTime(): LiveData<Boolean> {
        return userRepository.isFirstAppLaunch()
    }

    fun checkIfTokenAvailable(): LiveData<String> {
        return userRepository.getAuthToken()
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.removeAuthToken()
        }
    }

    fun registerUser(name: String, email: String, password: String) =
        userRepository.register(name, email, password)

    fun setFirstTime(firstTime: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            loginPreferences.setFirstTime(firstTime)
        }
    }

    fun addStory(
        token: String,
        imageFile: File,
        description: String,
        lat: Float? = null,
        lon: Float? = null
    ) = storyRepository.postStory(token, imageFile, description, lat, lon)
}
