package com.example.leran

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("/users")
    fun fittAllUsers(): Call<List<User>>


}