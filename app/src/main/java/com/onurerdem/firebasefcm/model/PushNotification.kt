package com.onurerdem.firebasefcm.model

data class PushNotification(
    var data : NotificationData,
    var to : String
)