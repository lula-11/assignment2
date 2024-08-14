package com.example.todoapp.data

data class AuthResponse(
    val id: String,
    val token: String
)


data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)


data class TodoItem(
    val description: String,
    val completed: Boolean,
    val id: String,
)

data class TodoRequest(
    val description: String,
    val completed: Boolean
)

