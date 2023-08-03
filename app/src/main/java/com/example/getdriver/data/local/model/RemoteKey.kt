package com.example.getdriver.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "movie_remote_keys")
data class RemoteKey(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val prevPage: Int?,
    val nextPage: Int?,
    val lastUpdated: Long?,
)