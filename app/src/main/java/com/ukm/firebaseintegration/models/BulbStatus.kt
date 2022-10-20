package com.ukm.firebaseintegration.models

import com.google.firebase.firestore.PropertyName

data class BulbStatus(
    @PropertyName("isOn")
    val isOn: Boolean = false,
)
