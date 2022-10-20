package com.ukm.firebaseintegration.models

import com.google.firebase.firestore.PropertyName

data class UsersData(
    @PropertyName("name")
    val name: String = "",
    @PropertyName("role")
    val role: String = "",
    @PropertyName("profilePicture")
    val profilePicture: String = ""
)
