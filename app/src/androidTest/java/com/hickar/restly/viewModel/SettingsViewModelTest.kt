package com.hickar.restly.viewModel

import androidx.lifecycle.SavedStateHandle
import com.hickar.restly.models.*
import com.hickar.restly.repository.room.CollectionRepository
import com.hickar.restly.services.AuthService
import com.hickar.restly.services.SharedPreferencesHelper
import com.hickar.restly.testUtils.random
import io.mockk.*
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.*

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SettingsViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    private val prefs = mockk<SharedPreferencesHelper>(relaxed = true)
    private val repository = mockk<CollectionRepository>()
    private val authService = mockk<AuthService>()

    private val requestPrefsChannel = Channel<RequestPrefs>()
    private val webviewPrefsChannel = Channel<WebViewPrefs>()
    private val postmanUserInfoChannel = Channel<PostmanUserInfo?>()

    @BeforeAll
    fun setupAll() {
        every { prefs.getRequestPrefs() } returns requestPrefsChannel.consumeAsFlow()
        every { prefs.getWebViewPrefs() } returns webviewPrefsChannel.consumeAsFlow()
        every { prefs.getPostmanUserInfo() } returns postmanUserInfoChannel.consumeAsFlow()
    }

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        clearMocks(repository, prefs, authService)
    }

    @Test
    fun testSuccessfulLoginToPostman() = runTest {
        val viewModel = buildTestableViewModel()
        val postmanApiKey = "".random()
        val postmanUserInfo = PostmanUserInfo()

        coEvery { prefs.setPostmanUserInfo(postmanUserInfo) } returns true
        coEvery { prefs.setPostmanApiKey(postmanApiKey) } returns true
        coEvery { authService.loginToPostman(postmanApiKey) } returns postmanUserInfo

        postmanUserInfoChannel.send(null)
        viewModel.loginToPostman(postmanApiKey)
        postmanUserInfoChannel.send(postmanUserInfo)

        assertEquals(postmanUserInfo, viewModel.postmanUserInfo.value)

        coVerify(exactly = 1) { prefs.setPostmanUserInfo(postmanUserInfo) }
        coVerify(exactly = 1) { prefs.setPostmanApiKey(postmanApiKey) }
        coVerify(exactly = 1) { authService.loginToPostman(postmanApiKey) }
    }

    @Test
    fun testWrongApiKeyLoginToPostman() = runTest {
        val viewModel = buildTestableViewModel()
        val postmanApiKey = "".random()

        coEvery { authService.loginToPostman(postmanApiKey) } throws WrongApiKeyException()

        viewModel.loginToPostman(postmanApiKey)

        assertEquals(ErrorEvent.PostmanAuthError, viewModel.error.value)

        coVerify(exactly = 0) { prefs.setPostmanUserInfo(any()) }
        coVerify(exactly = 0) { prefs.setPostmanApiKey(any()) }
        coVerify(exactly = 0) { repository.deleteRemoteCollections() }
    }

    @Test
    fun testNetworkUnavailableLoginToPostman() = runTest {
        val viewModel = buildTestableViewModel()
        val postmanApiKey = "".random()

        coEvery { authService.loginToPostman(postmanApiKey) } throws NetworkUnavailableException()

        viewModel.loginToPostman(postmanApiKey)

        assertEquals(ErrorEvent.NoInternetConnectionError, viewModel.error.value)

        coVerify(exactly = 0) { prefs.setPostmanUserInfo(any()) }
        coVerify(exactly = 0) { prefs.setPostmanApiKey(any()) }
        coVerify(exactly = 0) { repository.deleteRemoteCollections() }
    }

    @Test
    fun testLogoutFromPostmanKeepCollections() = runTest {
        val viewModel = buildTestableViewModel()

        coEvery { prefs.deletePostmanUserInfo() } returns true
        coEvery { prefs.deletePostmanApiKey() } returns true

        viewModel.logoutFromPostman(shouldDeleteRemoteCollections = false)
        postmanUserInfoChannel.send(null)

        assertEquals(null, viewModel.postmanUserInfo.value)

        coVerify(exactly = 0) { repository.deleteRemoteCollections() }
        coVerify(exactly = 1) { prefs.deletePostmanUserInfo() }
        coVerify(exactly = 1) { prefs.deletePostmanApiKey() }
    }

    @Test
    fun testLogoutFromPostmanDeleteCollections() = runTest {
        val viewModel = buildTestableViewModel()

        coEvery { repository.deleteRemoteCollections() } just Runs
        coEvery { prefs.deletePostmanUserInfo() } returns true
        coEvery { prefs.deletePostmanApiKey() } returns true

        viewModel.logoutFromPostman(shouldDeleteRemoteCollections = true)
        postmanUserInfoChannel.send(null)

        assertEquals(null, viewModel.postmanUserInfo.value)

        coVerify(exactly = 1) { repository.deleteRemoteCollections() }
        coVerify(exactly = 1) { prefs.deletePostmanUserInfo() }
        coVerify(exactly = 1) { prefs.deletePostmanApiKey() }
    }

    @Test
    fun testSetVerificationEnabled() = runTest {
        val initialRequestPrefs = RequestPrefs(sslVerificationEnabled = true)
        val modifiedRequestPrefs = initialRequestPrefs.copy(sslVerificationEnabled = false)

        val viewModel = buildTestableViewModel()

        coEvery { prefs.setRequestPrefs(modifiedRequestPrefs) } returns true

        requestPrefsChannel.send(initialRequestPrefs)
        viewModel.setRequestSslVerificationEnabled(false)
        requestPrefsChannel.send(modifiedRequestPrefs)

        assertEquals(modifiedRequestPrefs, viewModel.requestPrefs.value)

        coVerify(exactly = 1) { prefs.setRequestPrefs(modifiedRequestPrefs) }
        coVerify(exactly = 1) { prefs.getRequestPrefs() }
    }

    @Test
    fun testSetRequestMaxSize() = runTest {
        val initialRequestPrefs = RequestPrefs(maxSize = 1000)
        val modifiedRequestPrefs = initialRequestPrefs.copy(maxSize = 0)

        val viewModel = buildTestableViewModel()

        coEvery { prefs.setRequestPrefs(modifiedRequestPrefs) } returns true

        requestPrefsChannel.send(initialRequestPrefs)
        viewModel.setRequestMaxSize(0)
        requestPrefsChannel.send(modifiedRequestPrefs)

        assertEquals(modifiedRequestPrefs, viewModel.requestPrefs.value)

        coVerify(exactly = 1) { prefs.setRequestPrefs(modifiedRequestPrefs) }
        coVerify(exactly = 1) { prefs.getRequestPrefs() }
    }

    @Test
    fun testSetRequestTimeout() = runTest {
        val initialRequestPrefs = RequestPrefs(timeout = 1000)
        val modifiedRequestPrefs = initialRequestPrefs.copy(timeout = 0)

        val viewModel = buildTestableViewModel()

        coEvery { prefs.setRequestPrefs(modifiedRequestPrefs) } returns true

        requestPrefsChannel.send(initialRequestPrefs)
        viewModel.setRequestTimeout(0)
        requestPrefsChannel.send(modifiedRequestPrefs)

        assertEquals(modifiedRequestPrefs, viewModel.requestPrefs.value)

        coVerify(exactly = 1) { prefs.setRequestPrefs(modifiedRequestPrefs) }
        coVerify(exactly = 1) { prefs.getRequestPrefs() }
    }

    @Test
    fun testSetWebViewJavascriptEnabled() = runTest {
        val initialWebViewPrefs = WebViewPrefs(javascriptEnabled = false)
        val modifiedWebViewPrefs = initialWebViewPrefs.copy(javascriptEnabled = true)

        val viewModel = buildTestableViewModel()

        coEvery { prefs.setWebViewPrefs(modifiedWebViewPrefs) } returns true

        webviewPrefsChannel.send(initialWebViewPrefs)
        viewModel.setWebViewJavascriptEnabled(true)
        webviewPrefsChannel.send(modifiedWebViewPrefs)

        assertEquals(modifiedWebViewPrefs, viewModel.webViewPrefs.value)

        coVerify(exactly = 1) { prefs.setWebViewPrefs(modifiedWebViewPrefs) }
        coVerify(exactly = 1) { prefs.getWebViewPrefs() }
    }

    @Test
    fun testSetWebViewTextSize() = runTest {
        val initialWebViewPrefs = WebViewPrefs(textSize = 16)
        val modifiedWebViewPrefs = initialWebViewPrefs.copy(textSize = 32)

        val viewModel = buildTestableViewModel()

        coEvery { prefs.setWebViewPrefs(modifiedWebViewPrefs) } returns true

        webviewPrefsChannel.send(initialWebViewPrefs)
        viewModel.setWebViewTextSize(32)
        webviewPrefsChannel.send(modifiedWebViewPrefs)

        assertEquals(modifiedWebViewPrefs, viewModel.webViewPrefs.value)

        coVerify(exactly = 1) { prefs.setWebViewPrefs(modifiedWebViewPrefs) }
        coVerify(exactly = 1) { prefs.getWebViewPrefs() }
    }

    private fun buildTestableViewModel(): SettingsViewModel {
        return SettingsViewModel(SavedStateHandle(), prefs, repository, authService)
    }
}