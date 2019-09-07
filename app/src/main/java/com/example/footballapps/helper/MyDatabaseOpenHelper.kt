package com.example.footballapps.helper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.footballapps.favorite.FavoriteMatch
import org.jetbrains.anko.db.*

class MyDatabaseOpenHelper(ctx : Context) : ManagedSQLiteOpenHelper(ctx, "Favorite.db", null, 1){

    companion object{
        private var instance : MyDatabaseOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context) : MyDatabaseOpenHelper {
            if(instance == null){
                instance = MyDatabaseOpenHelper(ctx.applicationContext)
            }
            return instance as MyDatabaseOpenHelper
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(FavoriteMatch.TABLE_FAVORITE_MATCH, true,
            FavoriteMatch.ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
            FavoriteMatch.EVENT_ID to TEXT + UNIQUE,
            FavoriteMatch.LEAGUE_NAME to TEXT,
            FavoriteMatch.EVENT_DATE to TEXT,
            FavoriteMatch.LEAGUE_MATCH_WEEK to TEXT,
            FavoriteMatch.EVENT_TIME to TEXT,
            FavoriteMatch.HOME_TEAM_NAME to TEXT,
            FavoriteMatch.AWAY_TEAM_NAME to TEXT,
            FavoriteMatch.HOME_TEAM_SCORE to TEXT,
            FavoriteMatch.AWAY_TEAM_SCORE to TEXT)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.dropTable(FavoriteMatch.TABLE_FAVORITE_MATCH, true)
    }

}

val Context.database : MyDatabaseOpenHelper
    get() = MyDatabaseOpenHelper.getInstance(applicationContext)