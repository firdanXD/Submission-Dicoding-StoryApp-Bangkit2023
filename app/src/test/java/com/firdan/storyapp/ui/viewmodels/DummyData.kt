package com.firdan.storyapp.ui.viewmodels

import com.firdan.storyapp.data.local.entity.UserModel
import com.firdan.storyapp.data.local.entity.StoryRemoteKeys
import com.firdan.storyapp.data.remote.dataclass.LoginResponse
import com.firdan.storyapp.data.remote.dataclass.LoginResult

object DummyData {
    fun generateDummyStory(): List<UserModel> {
        val storyList = ArrayList<UserModel>()
        for (i in 1..10) {
            val story = UserModel(
                "profile.jpg",
                "2023-12-21T06:41:06.470Z",
                "Name $i",
                "description $i",
                100.0 + i*2,
                "story-$i",
                100.0 + i*2
            )

            storyList.add(story)
        }

        return storyList
    }

    fun generateListStory(): List<StoryRemoteKeys> {
        val storyKeysList = ArrayList<StoryRemoteKeys>()
        val data = generateDummyStory()
        data.slice(0..4).forEach {
            val id = it.id
            val key = StoryRemoteKeys(id, null, 2)
            storyKeysList.add(key)
        }

        data.slice(5..9).forEach {
            val id = it.id
            val key = StoryRemoteKeys(id, 1, 3)
            storyKeysList.add(key)
        }

        return storyKeysList
    }

    fun generateDummyLoginResponse(): LoginResponse {
        return LoginResponse(
            LoginResult("12345"),
            false,
            "success"
        )
    }

}
