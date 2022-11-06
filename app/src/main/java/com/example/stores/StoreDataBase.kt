package com.example.stores

import androidx.room.Database
import androidx.room.RoomDatabase

//tablas solo hay una storeentity
@Database(entities = arrayOf(StoreEntity::class), version = 2)
abstract class  StoreDataBase: RoomDatabase() {

    abstract fun storeDao(): StoreDao
}