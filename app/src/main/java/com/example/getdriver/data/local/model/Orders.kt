package com.example.getdriver.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "orders")
data class Orders(
    val from_addr: String,
    val to_addr: String,
    val longitude: String,
    val latitude: String,
    val cost: String,
    val user_id: Int
    )
{
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}