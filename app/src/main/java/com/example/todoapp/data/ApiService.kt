package com.example.todoapp.data

import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST
    suspend fun register(
        @Url url: String,
        @Body registerRequest: RegisterRequest
    ): Response<AuthResponse>

    @POST
    suspend fun login(
        @Url url: String,
        @Body loginRequest: LoginRequest
    ): Response<AuthResponse>

    @GET
    suspend fun getAllTodos(
        @Url url: String,
        @Header("Authorization") token: String
    ): Response<List<TodoItem>>

    @POST
    suspend fun createTodo(
        @Url url: String,
        @Body todoRequest: TodoRequest,
        @Header("Authorization") token: String
    ): Response<TodoItem>

    @PUT
    suspend fun updateTodo(
        @Url url: String,
        @Body updateRequest: TodoRequest,
        @Header("Authorization") token: String
    ): Response<TodoItem>
}
