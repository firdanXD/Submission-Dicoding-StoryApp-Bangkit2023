package com.firdan.storyapp.data.local.room

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.firdan.storyapp.data.local.entity.UserModel

@Dao
interface StoryDao {
    @Query("SELECT * FROM story")
    fun getStoriesAsLiveData(): LiveData<List<UserModel>>

    @Query("SELECT * FROM story")
    fun getAllStoriesPaging(): PagingSource<Int, UserModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStories(stories: List<UserModel>)

    @Query("DELETE FROM story")
    fun deleteAllStories()

    @Query("SELECT COUNT(id) FROM story")
    fun getStoriesCount(): Int
}