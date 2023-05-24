package com.vanka.suraksha.notifications

import com.vanka.suraksha.notifications.Constants.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {

    companion object {
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val api: NotificationInterface by lazy {
            retrofit.create(NotificationInterface::class.java)
        }
    }
}