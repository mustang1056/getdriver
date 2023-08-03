package com.example.getdriver.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.example.getdriver.data.local.model.Orders

@Dao
interface OrderDao {

    //@Query("SELECT * FROM remont ORDER BY id DESC")
    //fun getAllRemont() : PagingSource<Int, Remont>
    @Query("SELECT * FROM orders ORDER BY id DESC")
    fun getAllRemonts() : List<Orders>

    @Query("SELECT * FROM orders WHERE id = :id ORDER BY id DESC")
    fun getRemont(id: Int): List<Orders>

    @Query("DELETE FROM orders")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(orders: List<Orders>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(orders: Orders)

    @Query("SELECT * FROM orders ORDER BY id DESC")
    fun pagingSource(): PagingSource<Int, Orders>

}