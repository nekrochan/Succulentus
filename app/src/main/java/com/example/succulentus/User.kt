package com.example.succulentus

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val email: String,
    val password: String,
    val username: String
) : Parcelable