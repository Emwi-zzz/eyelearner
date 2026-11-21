package amu.wmi.eyelearner.auth.application

import amu.wmi.eyelearner.auth.application.usecase.FinalizeLoginUseCase
import amu.wmi.eyelearner.auth.application.usecase.InitiateLoginUseCase
import amu.wmi.eyelearner.auth.application.usecase.LogoutUseCase
import amu.wmi.eyelearner.auth.application.usecase.RefreshSessionUseCase
import amu.wmi.eyelearner.auth.domain.token.service.TokenGenerator
import amu.wmi.eyelearner.auth.domain.usos.exception.UsosApiException
import amu.wmi.eyelearner.auth.domain.usos.model.UsosAccessToken
import amu.wmi.eyelearner.auth.domain.usos.model.UsosRequestToken
import amu.wmi.eyelearner.auth.domain.usos.model.UsosUserProfile
import amu.wmi.eyelearner.auth.domain.usos.service.UsosOAuthClient
import amu.wmi.eyelearner.auth.dto.AuthSessionResponse
import amu.wmi.eyelearner.auth.dto.FinalizeLoginRequest
import amu.wmi.eyelearner.auth.dto.InitLoginRequest
import amu.wmi.eyelearner.auth.dto.InitLoginResponse
import amu.wmi.eyelearner.auth.dto.LogoutRequest
import amu.wmi.eyelearner.auth.dto.LogoutResponse
import amu.wmi.eyelearner.auth.dto.RefreshSessionRequest
import amu.wmi.eyelearner.auth.infrastructure.config.AuthProperties
import amu.wmi.eyelearner.auth.infrastructure.config.UsosProperties
import amu.wmi.eyelearner.auth.infrastructure.token.InMemoryTokenStorage
import amu.wmi.eyelearner.auth.infrastructure.usos.InMemoryOAuthRequestTokenStorage
import amu.wmi.eyelearner.user.domain.model.User
import amu.wmi.eyelearner.user.infrastructure.persistence.InMemoryUserRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration

class AuthServiceTest {

    private lateinit var userRepository: InMemoryUserRepository
    private lateinit var tokenStorage: InMemoryTokenStorage
    private lateinit var requestTokenStorage: InMemoryOAuthRequestTokenStorage
    private lateinit var fakeUsosClient: FakeUsosOAuthClient
    private lateinit var fakeTokenGenerator: FakeTokenGenerator
    private lateinit var authService: AuthService

    class FakeUsosOAuthClient : UsosOAuthClient {
        var requestTokenResult: Result<UsosRequestToken> = Result.success(
            UsosRequestToken("test-oauth-token", "test-token-secret", true)
        )
        var accessTokenResult: Result<UsosAccessToken> = Result.success(
            UsosAccessToken("test-access-token", "test-access-token-secret")
        )
        var userProfileResult: Result<UsosUserProfile> = Result.success(
            UsosUserProfile(id = "12345", firstName = "Jan", lastName = "Kowalski")
        )

        var lastCallbackUrl: String? = null
        var lastScopes: String? = null

        override suspend fun getRequestToken(callbackUrl: String, scopes: String?): UsosRequestToken {
            lastCallbackUrl = callbackUrl
            lastScopes = scopes
            return requestTokenResult.getOrThrow()
        }

        override suspend fun getAccessToken(
            oauthToken: String,
            oauthVerifier: String,
            requestTokenSecret: String
        ): UsosAccessToken {
            return accessTokenResult.getOrThrow()
        }

        override suspend fun getUserProfile(
            accessToken: String,
            accessTokenSecret: String
        ): UsosUserProfile {
            return userProfileResult.getOrThrow()
        }
    }

    class FakeTokenGenerator : TokenGenerator {
        private var counter = 0
        override fun generateToken(): String = "token-${++counter}"
    }

    @BeforeEach
    fun setup() {
        userRepository = InMemoryUserRepository()
        tokenStorage = InMemoryTokenStorage()
        requestTokenStorage = InMemoryOAuthRequestTokenStorage()
        fakeUsosClient = FakeUsosOAuthClient()
        fakeTokenGenerator = FakeTokenGenerator()

        val authProperties = AuthProperties(
            accessTokenExpirationSeconds = 900,
            refreshTokenExpirationSeconds = 604800
        )
        val usosProperties = UsosProperties(
            baseUrl = "https://usosapps.amu.edu.pl",
            consumerKey = "test-key",
            consumerSecret = "test-secret",
            scopes = ""
        )

        val initiateLoginUseCase = InitiateLoginUseCase(
            usosOAuthClient = fakeUsosClient,
            requestTokenStorage = requestTokenStorage,
            usosProperties = usosProperties
        )
        val finalizeLoginUseCase = FinalizeLoginUseCase(
            requestTokenStorage = requestTokenStorage,
            usosOAuthClient = fakeUsosClient,
            userRepository = userRepository,
            tokenStorage = tokenStorage,
            tokenGenerator = fakeTokenGenerator,
            authProperties = authProperties
        )
        val refreshSessionUseCase = RefreshSessionUseCase(
            tokenStorage = tokenStorage,
            userRepository = userRepository,
            tokenGenerator = fakeTokenGenerator,
            authProperties = authProperties
        )
        val logoutUseCase = LogoutUseCase(
            tokenStorage = tokenStorage
        )

        authService = AuthService(
            initiateLoginUseCase = initiateLoginUseCase,
            finalizeLoginUseCase = finalizeLoginUseCase,
            refreshSessionUseCase = refreshSessionUseCase,
            logoutUseCase = logoutUseCase
        )
    }

    @Test
    fun `initiate returns success and stores request token secret`() = runTest {
        val response = authService.initiate(InitLoginRequest("https://myapp.com/callback"))

        assertTrue(response is InitLoginResponse.Success)
        val success = response as InitLoginResponse.Success
        assertEquals("test-oauth-token", success.oauthToken)
        assertEquals(
            "https://usosapps.amu.edu.pl/services/oauth/authorize?oauth_token=test-oauth-token",
            success.authorizeUrl
        )

        // Verify request token secret was stored in requestTokenStorage
        val secret = requestTokenStorage.getAndRemoveRequestTokenSecret("test-oauth-token")
        assertEquals("test-token-secret", secret)
    }

    @Test
    fun `initiate returns Error on USOS exception`() = runTest {
        fakeUsosClient.requestTokenResult = Result.failure(UsosApiException("USOS unavailable", 502))

        val response = authService.initiate(InitLoginRequest("https://myapp.com/callback"))
        assertTrue(response is InitLoginResponse.Error)
        val error = response as InitLoginResponse.Error
        assertTrue(error.message.contains("USOS unavailable"))
    }

    @Test
    fun `finalize creates new user and issues tokens`() = runTest {
        // Pre-store request token secret as initiate would do
        requestTokenStorage.storeRequestTokenSecret("token-1", "secret-1", Duration.ofMinutes(15))

        val response = authService.finalize(FinalizeLoginRequest("token-1", "verifier-1"))
        assertTrue(response is AuthSessionResponse.Success)
        val success = response as AuthSessionResponse.Success
        assertEquals("token-1", success.accessToken)
        assertEquals("token-2", success.refreshToken)
        assertEquals(900L, success.expiresInSeconds)

        // Verify user was created
        val user = userRepository.findByUsosId("12345")
        assertNotNull(user)
        assertEquals("Jan", user?.firstName)
        assertEquals("Kowalski", user?.lastName)

        // Verify access token is active and valid
        val accessRecord = tokenStorage.findAccessToken("token-1")
        assertNotNull(accessRecord)
        assertEquals(user?.id, accessRecord?.userId)
    }

    @Test
    fun `finalize updates existing user info`() = runTest {
        val existing = User(
            id = "existing-id",
            usosId = "12345",
            firstName = "StareImie",
            lastName = "StareNazwisko"
        )
        userRepository.save(existing)
        requestTokenStorage.storeRequestTokenSecret("token-1", "secret-1", Duration.ofMinutes(15))

        val response = authService.finalize(FinalizeLoginRequest("token-1", "verifier-1"))
        assertTrue(response is AuthSessionResponse.Success)

        val updated = userRepository.findById("existing-id")
        assertEquals("Jan", updated?.firstName)
        assertEquals("Kowalski", updated?.lastName)
    }

    @Test
    fun `finalize with unknown oauth token returns InvalidOrExpiredToken`() = runTest {
        val response = authService.finalize(FinalizeLoginRequest("non-existent-token", "verifier-1"))
        assertTrue(response is AuthSessionResponse.InvalidOrExpiredToken)
    }

    @Test
    fun `finalize with invalid verifier returns InvalidOrExpiredToken`() = runTest {
        requestTokenStorage.storeRequestTokenSecret("token-1", "secret-1", Duration.ofMinutes(15))
        fakeUsosClient.accessTokenResult = Result.failure(UsosApiException("Invalid verifier", 400))

        val response = authService.finalize(FinalizeLoginRequest("token-1", "bad-verifier"))
        assertTrue(response is AuthSessionResponse.InvalidOrExpiredToken)
    }

    @Test
    fun `refresh rotates refresh token and revokes old tokens`() = runTest {
        val user = User("u1", "usos-1", "Jan", "Kowalski")
        userRepository.save(user)
        tokenStorage.storeAccessToken("old-access", "u1", Duration.ofMinutes(15))
        tokenStorage.storeRefreshToken("old-refresh", "u1", "old-access", Duration.ofDays(7))

        val response = authService.refresh(RefreshSessionRequest("old-refresh"))
        assertTrue(response is AuthSessionResponse.Success)
        val success = response as AuthSessionResponse.Success

        assertNotEquals("old-access", success.accessToken)
        assertNotEquals("old-refresh", success.refreshToken)

        // Verify old tokens are revoked
        assertNull(tokenStorage.findAccessToken("old-access"))
        assertNull(tokenStorage.findRefreshToken("old-refresh"))

        // Verify new tokens are stored
        assertNotNull(tokenStorage.findAccessToken(success.accessToken))
        assertNotNull(tokenStorage.findRefreshToken(success.refreshToken))
    }

    @Test
    fun `refresh with invalid refresh token returns InvalidOrExpiredToken`() = runTest {
        val response = authService.refresh(RefreshSessionRequest("invalid-refresh-token"))
        assertTrue(response is AuthSessionResponse.InvalidOrExpiredToken)
    }

    @Test
    fun `logout revokes refresh token and associated access token`() = runTest {
        tokenStorage.storeAccessToken("active-access", "u1", Duration.ofMinutes(15))
        tokenStorage.storeRefreshToken("active-refresh", "u1", "active-access", Duration.ofDays(7))

        val response = authService.logout(LogoutRequest("active-refresh"))
        assertEquals(LogoutResponse.Success, response)

        assertNull(tokenStorage.findAccessToken("active-access"))
        assertNull(tokenStorage.findRefreshToken("active-refresh"))
    }

    @Test
    fun `logout with non-existent token still returns Success`() = runTest {
        val response = authService.logout(LogoutRequest("already-revoked-token"))
        assertEquals(LogoutResponse.Success, response)
    }
}
