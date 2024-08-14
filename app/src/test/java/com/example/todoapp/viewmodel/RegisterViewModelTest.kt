import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.todoapp.data.ApiService
import com.example.todoapp.data.AuthResponse
import com.example.todoapp.data.RegisterRequest
import com.example.todoapp.viewmodel.RegisterViewModel
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
import retrofit2.Response

@ExperimentalCoroutinesApi
class RegisterViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var apiService: ApiService
    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        apiService = mockk()
        viewModel = RegisterViewModel(apiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `register should succeed with valid details`() = runTest {
        val name = "Test User"
        val email = "test@example.com"
        val password = "password"
        val authResponse = AuthResponse("123", "fake_token")
        val request = RegisterRequest(name, email, password)
        val response = Response.success(authResponse)

        coEvery { apiService.register(any(), request) } returns response

        var successResponse: AuthResponse? = null
        var errorResponse: String? = null

        viewModel.register(name, email, password, { successResponse = it }, { errorResponse = it })

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(authResponse, successResponse)
        assertEquals(null, errorResponse)
    }

    @Test
    fun `register should fail with invalid details`() = runTest {
        val name = "Test User"
        val email = "invalid@example.com"
        val password = "password"
        val request = RegisterRequest(name, email, password)
        val response = Response.error<AuthResponse>(400, mockk(relaxed = true))

        coEvery { apiService.register(any(), request) } returns response

        var successResponse: AuthResponse? = null
        var errorResponse: String? = null

        viewModel.register(name, email, password, { successResponse = it }, { errorResponse = it })

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(null, successResponse)
        assertEquals("Registration failed: ", errorResponse)
    }

    @Test
    fun `register should handle network error`() = runTest {
        val name = "Test User"
        val email = "test@example.com"
        val password = "password"
        val request = RegisterRequest(name, email, password)

        coEvery { apiService.register(any(), request) } throws Exception("Network error")

        var successResponse: AuthResponse? = null
        var errorResponse: String? = null

        viewModel.register(name, email, password, { successResponse = it }, { errorResponse = it })

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(null, successResponse)
        assertEquals("An error occurred: Network error", errorResponse)
    }
}
