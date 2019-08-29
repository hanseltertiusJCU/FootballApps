package com.example.footballapps.application

import android.app.Application
import android.content.res.Resources

class FootballApps : Application() {

    companion object {
        lateinit var mInstance: FootballApps
        lateinit var res: Resources
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        res = resources
    }


}