package com.mobile.wanda.promoter.network

import android.content.Context
import android.content.Intent
import android.support.annotation.NonNull
import android.support.annotation.Nullable
import com.mobile.wanda.promoter.Wanda
import com.mobile.wanda.promoter.activity.Login
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.io.IOException

/**
 * Created by kombo on 23/11/2017.
 */

//TODO sort out token refreshes or log out the user if need be
class TokenAuthenticator : Authenticator {

    @Nullable
    @Synchronized
    @Throws(IOException::class)
    override fun authenticate(@NonNull route: Route, @NonNull response: Response): Request? {

        val context: Context = Wanda.INSTANCE
        context.startActivity(Intent(context, Login::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        })

        return null
    }
}