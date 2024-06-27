package com.firdan.storyapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.firdan.storyapp.data.local.pref.StoryPref
import com.firdan.storyapp.data.local.room.StoryDatabase
import com.firdan.storyapp.data.remote.retrofit.ApiConfig
import com.firdan.storyapp.data.remote.retrofit.ApiService
import com.firdan.storyapp.data.repository.StoryRepository
import com.firdan.storyapp.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.IOException

object Injection {

    private const val LOGIN_PREFERENCES_NAME = "login"

    fun provideStoryRepository(context: Context): StoryRepository =
        StoryRepository.getInstance(provideApiService(), provideDatabase(context))

    fun provideUserRepository(context: Context): UserRepository =
        UserRepository.getInstance(provideApiService(), provideLoginPreferences(context))

    fun provideLoginPreferences(context: Context): StoryPref =
        StoryPref.getInstance(providePreferencesDataStore(context, CoroutineScope(Dispatchers.IO)))

    private fun providePreferencesDataStore(
        context: Context,
        coroutineScope: CoroutineScope
    ): DataStore<Preferences> = try {
        PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(produceNewData = { emptyPreferences() }),
            migrations = listOf(SharedPreferencesMigration(context, LOGIN_PREFERENCES_NAME)),
            scope = coroutineScope,
            produceFile = { context.preferencesDataStoreFile(LOGIN_PREFERENCES_NAME) }
        )
    } catch (e: IOException) {
        throw RuntimeException("Failed to create Preferences DataStore", e)
    }

    private fun provideApiService(): ApiService = ApiConfig.getApiService()

    private fun provideDatabase(context: Context): StoryDatabase =
        StoryDatabase.getInstance(context)
}
