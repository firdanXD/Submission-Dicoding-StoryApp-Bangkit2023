package com.firdan.storyapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stories_remote_keys")
data class StoryRemoteKeys(
    @PrimaryKey(autoGenerate = false)
    @field:ColumnInfo(name = "id")
    val id: String,

    @field:ColumnInfo(name = "prevKey")
    val prevKey: Int?,

    @field:ColumnInfo(name = "nextKey")
    val nextKey: Int?
)
