package com.example.todoapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.ApiService
import com.example.todoapp.data.TodoItem
import com.example.todoapp.data.TodoRequest
import com.example.todoapp.data.RetrofitInstance
import kotlinx.coroutines.launch

class TodoListViewModel : ViewModel() {
    private val api: ApiService = RetrofitInstance.api
    private val apiKey = "244fb58f-2edf-44a9-ab60-38efe35f4952"

    var todos = mutableStateListOf<TodoItem>()
        private set

    var showAddTodo by mutableStateOf(false)
        private set

    fun fetchAllTodos(token: String, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val url = "https://todos.simpleapi.dev/api/todos?apikey=$apiKey"
                val response = api.getAllTodos(url, "Bearer $token")
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    todos.clear()
                    responseBody?.let { todos.addAll(it) }
                } else {
                    onError("Failed to fetch todos: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                onError("An error occurred: ${e.message}")
            }
        }
    }

    fun addTodo(token: String, description: String, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val url = "https://todos.simpleapi.dev/api/todos?apikey=$apiKey"
                val response = api.createTodo(url, TodoRequest(description, false), "Bearer $token")
                if (response.isSuccessful) {
                    response.body()?.let { todos.add(it) }
                    fetchAllTodos(token, onError)
                } else {
                    onError("Failed to add todo: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                onError("An error occurred: ${e.message}")
            }
        }
    }

    fun updateTodoStatus(token: String, todoId: String, description: String, completed: Boolean, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val url = "https://todos.simpleapi.dev/api/todos/$todoId?apikey=$apiKey"
                val response = api.updateTodo(url, TodoRequest(description, completed), "Bearer $token")
                if (response.isSuccessful) {
                    response.body()?.let { updatedTodo ->
                        val index = todos.indexOfFirst { it.id == todoId }
                        if (index != -1) {
                            todos[index] = updatedTodo
                        }
                    }
                } else {
                    onError("Failed to update todo: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                onError("An error occurred: ${e.message}")
            }
        }
    }

    fun showAddTodo() {
        showAddTodo = true
    }

    fun hideAddTodo() {
        showAddTodo = false
    }
}

