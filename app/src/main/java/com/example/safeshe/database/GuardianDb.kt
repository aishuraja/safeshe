package com.example.safeshe.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Guardian::class],version = 1,exportSchema = false)
abstract class GuardianDb: RoomDatabase() {
    abstract fun guardianDatabaseDao():GuardianDao

    companion object{
        private var INSTANCE: GuardianDb?= null

        fun getInstance(context: Context):GuardianDb{
            if(INSTANCE==null){
                INSTANCE= Room.databaseBuilder(
                    context.applicationContext,
                    GuardianDb::class.java,
                    "GuardianDB"
                ).build()
            }
            return INSTANCE as GuardianDb
        }
    }
}