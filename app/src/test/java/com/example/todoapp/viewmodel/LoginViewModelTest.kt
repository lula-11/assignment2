import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.todoapp.data.ApiService
import com.example.todoapp.data.AuthResponse
import com.example.todoapp.data.LoginRequest
import com.example.todoapp.viewmodel.LoginViewModel
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
class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var apiService: ApiService
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        apiService = mockk()
        viewModel = LoginViewModel(apiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // 重置主调度器
    }

    @Test
    fun `login should succeed with valid credentials`() = runTest {
        val email = "test@example.com"
        val password = "password"
        val authResponse = AuthResponse("123", "fake_token")
        val request = LoginRequest(email, password)
        val response = Response.success(authResponse)

        coEvery { apiService.login(any(), request) } returns response

        var successResponse: AuthResponse? = null
        var errorResponse: String? = null

        viewModel.login(email, password, { successResponse = it }, { errorResponse = it })

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(authResponse, successResponse)
        assertEquals(null, errorResponse)
    }

    @Test
    fun `login should fail with invalid credentials`() = runTest {
        val email = "test@example.com"
        val password = "wrong_password"
        val request = LoginRequest(email, password)
        val response = Response.error<AuthResponse>(401, mockk(relaxed = true))

        coEvery { apiService.login(any(), request) } returns response

        var successResponse: AuthResponse? = null
        var errorResponse: String? = null

        viewModel.login(email, password, { successResponse = it }, { errorResponse = it })

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(null, successResponse)
        assertEquals("Invalid email or password", errorResponse)
    }

    @Test
    fun `login should fail with network error`() = runTest {
        val email = "test@example.com"
        val password = "password"
        val request = LoginRequest(email, password)

        coEvery { apiService.login(any(), request) } throws Exception("Network error")

        var successResponse: AuthResponse? = null
        var errorResponse: String? = null

        viewModel.login(email, password, { successResponse = it }, { errorResponse = it })

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(null, successResponse)
        assertEquals("An error occurred: Network error", errorResponse)
    }
}
