package com.example.getdriver.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.auto.getremont.data.local.dao.RemoteKeyDao
import com.example.getdriver.data.local.dao.OrderDao
import com.example.getdriver.data.local.model.Orders
import com.example.getdriver.data.local.model.RemoteKey
import com.example.getdriver.utils.Converters

@Database(entities = [Orders::class, RemoteKey::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun orderDao(): OrderDao
    abstract fun pageKeyDao(): RemoteKeyDao

    //abstract fun pageKeyDao(): PageKeyDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) { instance ?: buildDatabase(context).also { instance = it } }

        private fun buildDatabase(appContext: Context) =
            Room.databaseBuilder(appContext, AppDatabase::class.java, "orders")
                .fallbackToDestructiveMigration()
                .build()
    }

}