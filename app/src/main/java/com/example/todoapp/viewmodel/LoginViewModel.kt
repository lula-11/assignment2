package com.example.todoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.ApiService
import com.example.todoapp.data.LoginRequest
import com.example.todoapp.data.AuthResponse
import com.example.todoapp.data.RetrofitInstance
import kotlinx.coroutines.launch


class LoginViewModel(private val api: ApiService = RetrofitInstance.api) : ViewModel() {

    private val apiKey = "244fb58f-2edf-44a9-ab60-38efe35f4952"

    fun login(email: String, password: String, onSuccess: (AuthResponse) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val url = "https://todos.simpleapi.dev/api/users/login?apikey=$apiKey"
                val response = api.login(url, LoginRequest(email, password))
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else {
                    onError("Invalid email or password")
                }
            } catch (e: Exception) {
                onError("An error occurred: ${e.message}")
            }
        }
    }
}
