import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.todoapp.data.ApiService
import com.example.todoapp.data.TodoItem
import com.example.todoapp.viewmodel.TodoListViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
import retrofit2.Response

@ExperimentalCoroutinesApi
class TodoListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var apiService: ApiService
    private lateinit var viewModel: TodoListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        apiService = mockk()
        viewModel = TodoListViewModel(apiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchAllTodos should successfully fetch todos`() = runTest {
        val token = "fake_token"
        val todos = listOf(TodoItem("Test Todo", false, "1"))
        val response = Response.success(todos)

        coEvery { apiService.getAllTodos(any(), any()) } returns response

        var errorResponse: String? = null

        viewModel.fetchAllTodos(token) { errorResponse = it }

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(todos, viewModel.todos)
        assertEquals(null, errorResponse)
    }

    @Test
    fun `fetchAllTodos should handle error response`() = runTest {
        val token = "fake_token"
        val response = Response.error<List<TodoItem>>(400, mockk(relaxed = true))

        coEvery { apiService.getAllTodos(any(), any()) } returns response

        var errorResponse: String? = null

        viewModel.fetchAllTodos(token) { errorResponse = it }

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.todos.isEmpty())
        assertEquals("Failed to fetch todos: ", errorResponse)
    }

    @Test
    fun `addTodo should successfully add a todo`() = runTest {
        val token = "fake_token"
        val newTodo = TodoItem("New Todo", false, "2")
        val response = Response.success(newTodo)

        coEvery { apiService.createTodo(any(), any(), any()) } returns response
        coEvery { apiService.getAllTodos(any(), any()) } returns Response.success(listOf(newTodo))

        var errorResponse: String? = null

        viewModel.addTodo(token, "New Todo") { errorResponse = it }

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(listOf(newTodo), viewModel.todos)
        assertEquals(null, errorResponse)
    }

    @Test
    fun `addTodo should handle error response`() = runTest {
        val token = "fake_token"
        val response = Response.error<TodoItem>(400, mockk(relaxed = true))

        coEvery { apiService.createTodo(any(), any(), any()) } returns response

        var errorResponse: String? = null

        viewModel.addTodo(token, "New Todo") { errorResponse = it }

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.todos.isEmpty())
        assertEquals("Failed to add todo: ", errorResponse)
    }

    @Test
    fun `updateTodoStatus should successfully update a todo`() = runTest {
        val token = "fake_token"
        val updatedTodo = TodoItem("Updated Todo", true, "1")
        val response = Response.success(updatedTodo)

        coEvery { apiService.updateTodo(any(), any(), any()) } returns response
        viewModel.todos.add(TodoItem("Updated Todo", false, "1"))

        var errorResponse: String? = null

        viewModel.updateTodoStatus(token, "1", "Updated Todo", true) { errorResponse = it }

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(updatedTodo, viewModel.todos[0])
        assertEquals(null, errorResponse)
    }

    @Test
    fun `updateTodoStatus should handle error response`() = runTest {
        val token = "fake_token"
        val response = Response.error<TodoItem>(400, mockk(relaxed = true))

        coEvery { apiService.updateTodo(any(), any(), any()) } returns response
        viewModel.todos.add(TodoItem("Todo", false, "1"))

        var errorResponse: String? = null

        viewModel.updateTodoStatus(token, "1", "Todo", true) { errorResponse = it }

        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.todos[0].completed)
        assertEquals("Failed to update todo: ", errorResponse)
    }
}

