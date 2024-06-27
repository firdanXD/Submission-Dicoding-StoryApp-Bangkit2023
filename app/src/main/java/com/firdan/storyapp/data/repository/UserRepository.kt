package com.firdan.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.firdan.storyapp.data.remote.dataclass.LoginResponse
import com.firdan.storyapp.data.Result
import com.firdan.storyapp.data.local.pref.StoryPref
import com.firdan.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class UserRepository(
    private val apiService: ApiService,
    private val loginPreferences: StoryPref
) : CoroutineScope {

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            if (response.error) {
                emit(Result.Error(response.message))
            } else {
                emit(Result.Success(response))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun register(name: String, email: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(
                name,
                email,
                password
            )
            if (response.error) {
                emit(Result.Error(response.message))
            } else {
                emit(Result.Success(response))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getAuthToken(): LiveData<String> = loginPreferences.getToken().asLiveData()

    fun removeAuthToken() {
        launch(Dispatchers.IO) {
            loginPreferences.deleteToken()
        }
    }

    fun isFirstAppLaunch(): LiveData<Boolean> = loginPreferences.isFirstTime().asLiveData()

    fun saveAuthToken(token: String) {
        launch(Dispatchers.IO) {
            loginPreferences.saveToken(token)
        }
    }

    companion object {
        private val TAG = UserRepository::class.java.simpleName

        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(apiService: ApiService, loginPreferences: StoryPref) =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, loginPreferences)
            }.also { instance = it }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}