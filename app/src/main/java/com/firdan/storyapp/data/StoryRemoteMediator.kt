package com.firdan.storyapp.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.firdan.storyapp.data.local.entity.UserModel
import com.firdan.storyapp.data.local.entity.StoryRemoteKeys
import com.firdan.storyapp.data.remote.retrofit.ApiService
import com.firdan.storyapp.data.local.room.StoryDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val authToken: String,
    private val apiService: ApiService,
    private val database: StoryDatabase
) : RemoteMediator<Int, UserModel>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UserModel>
    ): MediatorResult {
        return try {
            withContext(Dispatchers.IO) {
                val page = when (loadType) {
                    LoadType.REFRESH -> {
                        val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                        remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
                    }
                    LoadType.APPEND -> {
                        val remoteKeys = getRemoteKeyForLastItem(state)
                        remoteKeys?.nextKey
                            ?: return@withContext MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    }
                    LoadType.PREPEND -> {
                        val remoteKeys = getRemoteKeyForFirstItem(state)
                        remoteKeys?.prevKey
                            ?: return@withContext MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    }
                }

                val responseData = apiService.getStories(authToken, page, state.config.pageSize).story
                val endOfPaginationReached = responseData.isEmpty()

                database.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        database.storyRemoteKeysDao().deleteRemoteKeys()
                        database.storyDao().deleteAllStories()
                    }

                    val prevPage = if (page == 1) null else page - 1
                    val nextPage = if (endOfPaginationReached) null else page + 1
                    val keys = responseData.map {
                        StoryRemoteKeys(id = it.id, prevKey = prevPage, nextKey = nextPage)
                    }

                    database.storyRemoteKeysDao().addRemoteKeys(keys)
                    database.storyDao().insertStories(responseData)
                }

                MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            }
        } catch (exception: Exception) {
            Log.d(TAG, "load to error: ${exception.message}")
            return MediatorResult.Error(exception)
        }
    }

    private fun getRemoteKeyForLastItem(state: PagingState<Int, UserModel>): StoryRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.storyRemoteKeysDao().getRemoteKeys(data.id)
        }
    }

    private fun getRemoteKeyForFirstItem(state: PagingState<Int, UserModel>): StoryRemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.storyRemoteKeysDao().getRemoteKeys(data.id)
        }
    }

    private fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, UserModel>): StoryRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.storyRemoteKeysDao().getRemoteKeys(id)
            }
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
        const val TAG = "StoryRemoteMediator"
    }
}
