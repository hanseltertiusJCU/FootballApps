package com.example.footballapps.helper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.footballapps.favorite.FavoriteMatchItem
import com.example.footballapps.favorite.FavoriteTeamItem
import org.jetbrains.anko.db.*

class MyDatabaseOpenHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "Favorite.db", null, 1) {

    companion object {
        private var instance: MyDatabaseOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): MyDatabaseOpenHelper {
            if (instance == null) {
                instance = MyDatabaseOpenHelper(ctx.applicationContext)
            }
            return instance as MyDatabaseOpenHelper
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(
            FavoriteMatchItem.TABLE_FAVORITE_MATCH, true,
            FavoriteMatchItem.ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
            FavoriteMatchItem.EVENT_ID to TEXT + UNIQUE,
            FavoriteMatchItem.EVENT_NAME to TEXT,
            FavoriteMatchItem.EVENT_DATE to TEXT,
            FavoriteMatchItem.EVENT_TIME to TEXT,
            FavoriteMatchItem.LEAGUE_NAME to TEXT,
            FavoriteMatchItem.LEAGUE_MATCH_WEEK to TEXT,
            FavoriteMatchItem.HOME_TEAM_ID to TEXT,
            FavoriteMatchItem.AWAY_TEAM_ID to TEXT,
            FavoriteMatchItem.HOME_TEAM_NAME to TEXT,
            FavoriteMatchItem.AWAY_TEAM_NAME to TEXT,
            FavoriteMatchItem.HOME_TEAM_SCORE to TEXT,
            FavoriteMatchItem.AWAY_TEAM_SCORE to TEXT
        )

        db.createTable(
            FavoriteTeamItem.TABLE_FAVORITE_TEAM, true,
            FavoriteTeamItem.ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
            FavoriteTeamItem.TEAM_ID to TEXT + UNIQUE,
            FavoriteTeamItem.TEAM_NAME to TEXT,
            FavoriteTeamItem.TEAM_BADGE_URL to TEXT
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.dropTable(FavoriteMatchItem.TABLE_FAVORITE_MATCH, true)
        db.dropTable(FavoriteTeamItem.TABLE_FAVORITE_TEAM, true)
    }

}

val Context.database: MyDatabaseOpenHelper
    get() = MyDatabaseOpenHelper.getInstance(applicationContext)