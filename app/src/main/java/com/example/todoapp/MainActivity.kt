package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.material3.*
import com.example.todoapp.data.AuthResponse
import com.example.todoapp.viewmodel.LoginViewModel
import com.example.todoapp.viewmodel.RegisterViewModel
import com.example.todoapp.viewmodel.TodoListViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.todoapp.data.TodoItem

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoApp()
        }
    }
}

enum class Screen {
    LOGIN, CREATE_ACCOUNT, MAIN
}

@Composable
fun TodoApp() {
    var currentScreen by remember { mutableStateOf(Screen.LOGIN) }
    var userId by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }

    when (currentScreen) {
        Screen.LOGIN -> LoginScreen(
            onNavigateToCreateAccount = {
                currentScreen = Screen.CREATE_ACCOUNT
            },
            onLoginSuccess = { authResponse ->
                userId = authResponse.id
                token = authResponse.token
                currentScreen = Screen.MAIN
            }
        )

        Screen.CREATE_ACCOUNT -> CreateAccountScreen(
            onNavigateToLogin = {
                currentScreen = Screen.LOGIN
            },
            onCreateAccountSuccess = { authResponse ->
                userId = authResponse.id
                token = authResponse.token
                currentScreen = Screen.MAIN
            }
        )

        Screen.MAIN -> MainScreen(token)
    }
}

@Composable
fun LoginScreen(onNavigateToCreateAccount: () -> Unit, onLoginSuccess: (AuthResponse) -> Unit) {
    val viewModel: LoginViewModel = viewModel()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var displayError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LoginContent(
        email = email,
        onEmailChange = { email = it },
        password = password,
        onPasswordChange = { password = it },
        displayError = displayError,
        errorMessage = errorMessage,
        onLoginClick = {
            if (email.isNotBlank() && password.isNotBlank()) {
                displayError = false
                viewModel.login(email, password, onLoginSuccess) { error ->
                    displayError = true
                    errorMessage = error
                }
            } else {
                displayError = true
                errorMessage = "Please enter email and password"
            }
        },
        onNavigateToCreateAccount = onNavigateToCreateAccount
    )
}

@Composable
fun LoginContent(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    displayError: Boolean,
    errorMessage: String,
    onLoginClick: () -> Unit,
    onNavigateToCreateAccount: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text(stringResource(R.string.email_address)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text(stringResource(R.string.password)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (displayError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.log_in))
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onNavigateToCreateAccount) {
            Text(stringResource(R.string.create_an_account))
        }
    }
}

@Composable
fun CreateAccountScreen(onNavigateToLogin: () -> Unit, onCreateAccountSuccess: (AuthResponse) -> Unit) {
    val viewModel: RegisterViewModel = viewModel()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var displayError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    CreateAccountContent(
        name = name,
        onNameChange = { name = it },
        email = email,
        onEmailChange = { email = it },
        password = password,
        onPasswordChange = { password = it },
        displayError = displayError,
        errorMessage = errorMessage,
        onCreateAccountClick = {
            if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                displayError = false
                viewModel.register(name, email, password, onCreateAccountSuccess) { error ->
                    displayError = true
                    errorMessage = error
                }
            } else {
                displayError = true
                errorMessage = "Please enter name, email, and password"
            }
        },
        onNavigateToLogin = onNavigateToLogin
    )
}

@Composable
fun CreateAccountContent(
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    displayError: Boolean,
    errorMessage: String,
    onCreateAccountClick: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(stringResource(R.string.name)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text(stringResource(R.string.email_address)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text(stringResource(R.string.password)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (displayError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onCreateAccountClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.create_account))
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onNavigateToLogin) {
            Text(stringResource(R.string.log_in))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(token: String) {
    val viewModel: TodoListViewModel = viewModel()
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.fetchAllTodos(token) { error ->
            errorMessage = error
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.todo)) },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.showAddTodo() }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_todo))
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (errorMessage.isNotBlank()) {
                AlertDialog(
                    onDismissRequest = { errorMessage = "" },
                    confirmButton = {
                        TextButton(onClick = { errorMessage = "" }) {
                            Text(stringResource(R.string.ok))
                        }
                    },
                    text = { Text(errorMessage) }
                )
            }

            TodoList(
                todos = viewModel.todos,
                onTodoCheckedChange = { todo, checked ->
                    viewModel.updateTodoStatus(token, todo.id, todo.description, checked) { error ->
                        errorMessage = error
                    }
                }
            )

            if (viewModel.showAddTodo) {
                AddTodo(
                    onDismiss = { viewModel.hideAddTodo() },
                    onAddTodo = { description ->
                        viewModel.addTodo(token, description) { error ->
                            errorMessage = error
                        }
                        viewModel.hideAddTodo()
                    }
                )
            }
        }
    }
}

@Composable
fun TodoList(
    todos: List<TodoItem>,
    onTodoCheckedChange: (TodoItem, Boolean) -> Unit
) {
    LazyColumn {
        items(todos) { todo ->
            TodoItemRow(
                todo = todo,
                onCheckedChange = { checked ->
                    onTodoCheckedChange(todo, checked)
                }
            )
        }
    }
}

@Composable
fun TodoItemRow(
    todo: TodoItem,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(todo.description)
        Spacer(modifier = Modifier.weight(1f))
        Checkbox(
            checked = todo.completed,
            onCheckedChange = onCheckedChange
        )
    }
}


@Composable
fun AddTodo(
    onDismiss: () -> Unit,
    onAddTodo: (String) -> Unit
) {
    var description by remember { mutableStateOf("") }
    var displayError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    AddTodoContent(
        description = description,
        onDescriptionChange = { description = it },
        displayError = displayError,
        errorMessage = errorMessage,
        onSaveClick = {
            if (description.isNotBlank()) {
                onAddTodo(description)
                onDismiss()
            } else {
                displayError = true
                errorMessage = "Please enter a description for the todo item."
            }
        },
        onDismiss = onDismiss
    )
}

@Composable
fun AddTodoContent(
    description: String,
    onDescriptionChange: (String) -> Unit,
    displayError: Boolean,
    errorMessage: String,
    onSaveClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background,
            contentColor = contentColorFor(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text(stringResource(R.string.todo_item_description)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (displayError) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onSaveClick) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    }
}

