package com.example.todoapp.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.ApiService
import com.example.todoapp.data.RegisterRequest
import com.example.todoapp.data.AuthResponse
import com.example.todoapp.data.RetrofitInstance
import kotlinx.coroutines.launch

class RegisterViewModel(private val api: ApiService = RetrofitInstance.api) : ViewModel() {
    private val apiKey = "244fb58f-2edf-44a9-ab60-38efe35f4952"

    fun register(name: String, email: String, password: String, onSuccess: (AuthResponse) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val url = "https://todos.simpleapi.dev/api/users/register?apikey=$apiKey"
                val response = api.register(url, RegisterRequest(name, email, password))
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) } ?: onError("Registration failed")
                } else {
                    onError("Registration failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                onError("An error occurred: ${e.message}")
            }
        }
    }
}
