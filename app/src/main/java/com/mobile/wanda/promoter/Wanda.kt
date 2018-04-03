package com.mobile.wanda.promoter

import android.content.Context
import android.support.multidex.MultiDexApplication
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mobile.wanda.promoter.model.responses.AuthCredentials
import com.mobile.wanda.promoter.util.PrefUtils
import io.realm.Realm
import io.realm.RealmConfiguration
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

/**
 * Created by kombo on 23/11/2017.
 */
class Wanda : MultiDexApplication() {

    companion object {
        lateinit var INSTANCE: Wanda
    }

    init {
        INSTANCE = this
    }

    override fun onCreate() {
        super.onCreate()

        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Vollkorn-SemiBold.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )

        Realm.init(this)
        Realm.setDefaultConfiguration(realmConfig())
    }

    fun realmConfig(): RealmConfiguration {
        return RealmConfiguration.Builder()
                .name("Wanda") //hardcoded string just in case app name may change and thus preserve Realm's integrity and orderDetails
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

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
}