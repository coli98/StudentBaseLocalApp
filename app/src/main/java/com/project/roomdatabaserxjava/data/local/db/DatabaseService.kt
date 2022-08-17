package com.project.roomdatabaserxjava.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.project.roomdatabaserxjava.data.local.dao.StudentDao
import com.project.roomdatabaserxjava.data.local.entity.StudentEntity

@Database(entities = [StudentEntity::class], version = 1, exportSchema = false)

abstract class DatabaseService : RoomDatabase() {

    abstract fun studentDao(): StudentDao

    companion object {

        @Volatile
        private var INSTANCE: DatabaseService? = null

        fun getInstance(context: Context):DatabaseService{
            synchronized(this){
                var instance = INSTANCE

                if(instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        DatabaseService::class.java,
                    "student_demo"
                    )
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance

                }
                return instance
            }

        }

    }

}