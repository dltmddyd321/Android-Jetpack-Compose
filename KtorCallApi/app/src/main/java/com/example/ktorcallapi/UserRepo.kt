package com.example.ktorcallapi

import io.ktor.client.request.*

object UserRepo {

    suspend fun fetchUsers() : List<User> {

        val url = "https://615075ada706cd00179b745c.mockapi.io/users"

        return KtorClient.httpClient.get(url)
    }
}