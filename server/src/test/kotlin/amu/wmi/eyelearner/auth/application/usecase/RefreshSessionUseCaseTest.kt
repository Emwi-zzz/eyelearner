package amu.wmi.eyelearner.auth.application.usecase

import amu.wmi.eyelearner.auth.domain.token.service.TokenGenerator
import amu.wmi.eyelearner.auth.dto.AuthSessionResponse
import amu.wmi.eyelearner.auth.dto.RefreshSessionRequest
import amu.wmi.eyelearner.auth.infrastructure.config.AuthProperties
import amu.wmi.eyelearner.auth.infrastructure.token.InMemoryTokenStorage
import amu.wmi.eyelearner.user.domain.model.User
import amu.wmi.eyelearner.user.domain.repository.UserRepository
import amu.wmi.eyelearner.user.infrastructure.persistence.InMemoryUserRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration

class RefreshSessionUseCaseTest {

    private lateinit var tokenStorage: InMemoryTokenStorage
    private lateinit var userRepository: UserRepository
    private lateinit var tokenGenerator: TokenGenerator
    private lateinit var authProperties: AuthProperties
    private lateinit var useCase: RefreshSessionUseCase

    @BeforeEach
    fun setup() {
        tokenStorage = InMemoryTokenStorage()
        userRepository = InMemoryUserRepository()
        tokenGenerator = object : TokenGenerator {
            private var i = 0
            override fun generateToken(): String = "new-tok-${++i}"
        }
        authProperties = AuthProperties()
        useCase = RefreshSessionUseCase(tokenStorage, userRepository, tokenGenerator, authProperties)
    }

    @Test
    fun `rotates tokens successfully for valid refresh token`() = runTest {
        val user = User("u-1", "usos-1", "Jan", "Kowalski")
        userRepository.save(user)
        tokenStorage.storeAccessToken("old-access", "u-1", Duration.ofMinutes(15))
        tokenStorage.storeRefreshToken("old-refresh", "u-1", "old-access", Duration.ofDays(7))

        val response = useCase.execute(RefreshSessionRequest("old-refresh"))
        assertTrue(response is AuthSessionResponse.Success)
        val success = response as AuthSessionResponse.Success
        assertEquals("new-tok-1", success.accessToken)
        assertEquals("new-tok-2", success.refreshToken)

        assertNull(tokenStorage.findAccessToken("old-access"))
        assertNull(tokenStorage.findRefreshToken("old-refresh"))
    }

    @Test
    fun `returns InvalidOrExpiredToken for unknown refresh token`() = runTest {
        val response = useCase.execute(RefreshSessionRequest("unknown-refresh"))
        assertTrue(response is AuthSessionResponse.InvalidOrExpiredToken)
    }
}
