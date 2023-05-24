package com.vanka.suraksha.notifications

import com.vanka.suraksha.model.PushNotification
import com.vanka.suraksha.notifications.Constants.Companion.CONTENT_TYPE
import com.vanka.suraksha.notifications.Constants.Companion.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
interface NotificationInterface {

    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}