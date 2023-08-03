package com.auto.getremont.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.getdriver.data.local.model.RemoteKey

@Dao
interface RemoteKeyDao {
    @Query("SELECT * FROM movie_remote_keys WHERE id = :movieId")
    suspend fun getMovieRemoteKeys(movieId: Int): RemoteKey?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllMovieRemoteKeys(movieRemoteKeys : List<RemoteKey>)

    @Query("DELETE FROM movie_remote_keys")
    suspend fun deleteAllMovieRemoteKeys()
}