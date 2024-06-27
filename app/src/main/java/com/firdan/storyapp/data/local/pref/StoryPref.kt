package com.firdan.storyapp.data.local.pref

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StoryPref (private val dataStore: DataStore<Preferences>) {

    private val tokenKey = stringPreferencesKey("token")
    private val firstTimeKey = booleanPreferencesKey("first_time")

    fun getToken(): Flow<String> {
        return dataStore.data.map {
            it[tokenKey] ?: "null"
        }
    }

    fun isFirstTime(): Flow<Boolean> {
        return dataStore.data.map {
            it[firstTimeKey] ?: true
        }
    }

    suspend fun saveToken(token: String) {
        dataStore.edit {
            it[tokenKey] = token
        }
    }

    suspend fun setFirstTime(firstTime: Boolean) {
        dataStore.edit {
            it[firstTimeKey] = firstTime
        }
    }

    suspend fun deleteToken() {
        dataStore.edit {
            it[tokenKey] = "null"
        }
    }

    companion object {
        @Volatile
        private var instance: StoryPref? = null

        fun getInstance(dataStore: DataStore<Preferences>): StoryPref =
            instance ?: synchronized(this) {
                instance ?: StoryPref(dataStore)
            }.also { instance = it }
    }
}