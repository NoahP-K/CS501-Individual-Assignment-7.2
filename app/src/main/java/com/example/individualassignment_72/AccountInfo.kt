package com.example.individualassignment_72

import com.squareup.moshi.Json

data class Owner(
    @Json(name = "login") val login: String,
    @Json(name = "avatar_url") val avatar: String
)

data class Repo(
    val owner: Owner,
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String?
)