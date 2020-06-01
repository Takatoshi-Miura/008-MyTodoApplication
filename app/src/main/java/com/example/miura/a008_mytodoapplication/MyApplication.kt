package com.example.miura.a008_mytodoapplication

import android.app.Application
import io.realm.Realm

class MyApplication:Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }


}