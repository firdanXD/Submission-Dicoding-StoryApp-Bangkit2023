package com.firdan.storyapp.data.remote.dataclass


import com.firdan.storyapp.data.local.entity.UserModel
import com.google.gson.annotations.SerializedName


data class StoriesResponse(

    @field:SerializedName("listStory")
    val story: List<UserModel>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)


