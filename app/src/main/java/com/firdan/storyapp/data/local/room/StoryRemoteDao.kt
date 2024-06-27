package com.firdan.storyapp.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.firdan.storyapp.data.local.entity.StoryRemoteKeys

@Dao
interface StoryRemoteDao {

    @Query("SELECT * FROM stories_remote_keys WHERE id = :id")
    fun getRemoteKeys(id: String): StoryRemoteKeys?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addRemoteKeys(remoteKeys: List<StoryRemoteKeys>)

    @Query("DELETE FROM stories_remote_keys")
    fun deleteRemoteKeys()
}
