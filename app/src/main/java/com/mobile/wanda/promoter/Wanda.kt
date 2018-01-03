package com.mobile.wanda.promoter

import android.app.Application
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mobile.wanda.promoter.model.responses.AuthCredentials
import com.mobile.wanda.promoter.util.PrefUtils
import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by kombo on 23/11/2017.
 */
class Wanda : Application() {

    companion object {
        lateinit var INSTANCE: Wanda
    }

    init {
        INSTANCE = this
    }

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        Realm.setDefaultConfiguration(realmConfig())
    }

    fun realmConfig(): RealmConfiguration {
        return RealmConfiguration.Builder()
                .name("Wanda") //hardcoded string just in case app name may change and thus preserve Realm's integrity and data
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build()
    }

    fun getCredentials(): AuthCredentials {
        val authCredentials: String? = PrefUtils.getString(PrefUtils.CREDENTIALS, "")

        val gson = Gson()
        val type = object : TypeToken<AuthCredentials>() {}.type

        return gson.fromJson<AuthCredentials>(authCredentials, type)
    }
}