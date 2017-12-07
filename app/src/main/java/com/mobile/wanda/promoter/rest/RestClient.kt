package com.mobile.wanda.promoter.rest

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.mobile.wanda.promoter.Wanda
import io.realm.RealmObject
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by kombo on 23/11/2017.
 */
object RestClient {

    private val BASE_URL = "http://35.176.127.140/wanda-mobile/api/mobile/v1/"
    private lateinit var retrofit: Retrofit
    private val tokenAuthenticator = TokenAuthenticator()
    private val dispatcher = Dispatcher()
    private val gson = GsonBuilder()
            .setExclusionStrategies(object : ExclusionStrategy {
                override fun shouldSkipField(f: FieldAttributes): Boolean =
                        f.declaringClass == RealmObject::class.java

                override fun shouldSkipClass(clazz: Class<*>): Boolean = false
            }).create()

    private val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    /**
     * This is the main retrofit client that will be used app-wide for all requests that require token authentication
     * Attached are interceptors and the token authenticator which takes care of refreshing access tokens
     **/
    val client: Retrofit
        get() {
            dispatcher.maxRequests = 1

            val okClient = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor { chain ->
                        val original = chain.request()

                        val request = original.newBuilder()
                                .addHeader("Authorization", "Bearer " + Wanda.INSTANCE.getCredentials().accessToken)
                                .addHeader("Content-Type", "application/json")
                                .build()

                        chain.proceed(request)
                    }
                    .authenticator(tokenAuthenticator)
                    .addInterceptor(loggingInterceptor)
                    .dispatcher(dispatcher)
                    .build()

            retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okClient)
                    .build()
            return retrofit
        }

    /**
     * This client is only used at login since the login request doesn't require token authentication
     */
    val headerLessClient: Retrofit
        get() {
            dispatcher.maxRequests = 1

            val okClient = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor { chain ->
                        val original = chain.request()

                        val request = original.newBuilder()
                                .addHeader("Content-Type", "application/json")
                                .build()

                        chain.proceed(request)
                    }
                    .authenticator(tokenAuthenticator)
                    .addInterceptor(loggingInterceptor)
                    .dispatcher(dispatcher)
                    .build()

            retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okClient)
                    .build()
            return retrofit
        }
}