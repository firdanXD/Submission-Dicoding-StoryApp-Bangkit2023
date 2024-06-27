package com.firdan.storyapp.data.remote.retrofit

import com.firdan.storyapp.data.remote.dataclass.StoryResponse
import com.firdan.storyapp.data.remote.dataclass.LoginResponse
import com.firdan.storyapp.data.remote.dataclass.RegisterResponse
import com.firdan.storyapp.data.remote.dataclass.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.*

interface ApiService {
    companion object {
        const val STORIES_ENDPOINT = "stories"
        const val LOGIN_ENDPOINT = "login"
        const val REGISTER_ENDPOINT = "register"
    }

    @GET(STORIES_ENDPOINT)
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int = 1,
    ): StoriesResponse

    @Multipart
    @POST(STORIES_ENDPOINT)
    suspend fun postStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null
    ): StoryResponse

    @FormUrlEncoded
    @POST(LOGIN_ENDPOINT)
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST(REGISTER_ENDPOINT)
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse
}
