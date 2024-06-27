package com.firdan.storyapp.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.firdan.storyapp.data.local.pref.StoryPref
import com.firdan.storyapp.data.repository.StoryRepository
import com.firdan.storyapp.data.repository.UserRepository
import com.firdan.storyapp.di.Injection
import com.firdan.storyapp.ui.viewmodels.MainViewModel
import com.firdan.storyapp.ui.viewmodels.StoryMapViewModel

class ViewModelFactory private constructor(
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository,
    private val loginPreferences: StoryPref
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {

            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(storyRepository, userRepository, loginPreferences) as T
            }
            modelClass.isAssignableFrom(StoryMapViewModel::class.java) -> {
                StoryMapViewModel(storyRepository, userRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory = instance ?: synchronized(this) {
            instance ?: ViewModelFactory(
                Injection.provideStoryRepository(context),
                Injection.provideUserRepository(context),
                Injection.provideLoginPreferences(context)
            )
        }.also { instance = it }
    }
}
