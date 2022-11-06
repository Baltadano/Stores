package com.example.stores

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class StoreAplication : Application() {

    //implementando patron singleton
    //para acceder a la base descde cuqluier parte
    // de la aplicacion
    //object nos configura el patroon singleton
    // y la palabra companion que sera accesible desde cuqluieri parte de la app
    companion object {//euivalente a static
        lateinit var dataBase: StoreDataBase

        }

    override fun onCreate() {
        super.onCreate()

        val MIGRATION_1_2 = object : Migration(1,2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE StoreEntity ADD COLUMN photoUrl TEXT NOT NULL DEFAULT '' ")
            }
        }

        dataBase = Room.databaseBuilder(this,
            StoreDataBase::class.java,
            "TiendasDB")
            .addMigrations(MIGRATION_1_2)
            .build()
    }
    }
