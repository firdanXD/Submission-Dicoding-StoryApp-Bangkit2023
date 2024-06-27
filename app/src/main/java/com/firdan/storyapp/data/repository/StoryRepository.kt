package com.firdan.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.firdan.storyapp.data.Result
import com.firdan.storyapp.data.local.entity.UserModel
import com.firdan.storyapp.data.StoryRemoteMediator
import com.firdan.storyapp.data.local.room.StoryDatabase
import com.firdan.storyapp.data.remote.retrofit.ApiService
import com.firdan.storyapp.data.remote.dataclass.StoryResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException

class StoryRepository(
    private val apiService: ApiService,
    private val database: StoryDatabase
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getStories(userToken: String): LiveData<PagingData<UserModel>> {
        val pagingSourceFactory = { database.storyDao().getAllStoriesPaging() }

        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            remoteMediator = StoryRemoteMediator(
                userToken,
                apiService,
                database
            ),
            pagingSourceFactory = pagingSourceFactory
        ).liveData
    }

    fun getStoriesWithLocation(userToken: String, page: Int, size: Int): LiveData<Result<List<UserModel>>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.getStories(userToken, page, size)
                if (response.error) {
                    emit(Result.Error(response.message))
                } else {
                    val stories = response.story
                    emit(Result.Success(stories))
                }
            } catch (e: IOException) {
                emit(Result.Error("Network error. Please check your connection."))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }


    fun postStory(
        userToken: String,
        imageFile: File,
        description: String,
        latitude: Float?,
        longitude: Float?
    ): LiveData<Result<StoryResponse>> = liveData {
        emit(Result.Loading)

        val textPlainMediaType = "text/plain".toMediaType()
        val imageMediaType = "image/jpeg".toMediaTypeOrNull()

        val imagePart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            imageFile.asRequestBody(imageMediaType)
        )
        val descriptionRequestBody = description.toRequestBody(textPlainMediaType)
        val latitudeRequestBody = latitude.toString().toRequestBody(textPlainMediaType)
        val longitudeRequestBody = longitude.toString().toRequestBody(textPlainMediaType)

        try {
            val response = apiService.postStory(
                userToken,
                imagePart,
                descriptionRequestBody,
                latitudeRequestBody,
                longitudeRequestBody
            )

            if (response.error) {
                emit(Result.Error(response.message))
            } else {
                emit(Result.Success(response))
            }
        } catch (e: IOException) {
            emit(Result.Error("Network error. Please check your connection."))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    companion object {
        private val TAG = StoryRepository::class.java.simpleName
        private const val POST_ERROR_MESSAGE = "Story was not posted, please try again later."

        @Volatile
        private var instance: StoryRepository? = null

        @JvmStatic
        fun getInstance(
            apiService: ApiService,
            database: StoryDatabase
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, database)
            }.also { instance = it }
    }
}
