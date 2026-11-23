package amu.wmi.eyelearner.auth.application.usecase

import amu.wmi.eyelearner.auth.domain.token.service.TokenGenerator
import amu.wmi.eyelearner.auth.domain.usos.model.UsosAccessToken
import amu.wmi.eyelearner.auth.domain.usos.model.UsosUserProfile
import amu.wmi.eyelearner.auth.domain.usos.service.UsosOAuthClient
import amu.wmi.eyelearner.auth.dto.AuthSessionResponse
import amu.wmi.eyelearner.auth.dto.FinalizeLoginRequest
import amu.wmi.eyelearner.auth.infrastructure.config.AuthProperties
import amu.wmi.eyelearner.auth.infrastructure.token.InMemoryTokenStorage
import amu.wmi.eyelearner.auth.infrastructure.usos.InMemoryOAuthRequestTokenStorage
import amu.wmi.eyelearner.user.domain.repository.UserRepository
import amu.wmi.eyelearner.user.infrastructure.persistence.InMemoryUserRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import java.time.Duration

class FinalizeLoginUseCaseTest {

    private lateinit var requestTokenStorage: InMemoryOAuthRequestTokenStorage
    private lateinit var usosOAuthClient: UsosOAuthClient
    private lateinit var userRepository: UserRepository
    private lateinit var tokenStorage: InMemoryTokenStorage
    private lateinit var tokenGenerator: TokenGenerator
    private lateinit var authProperties: AuthProperties
    private lateinit var useCase: FinalizeLoginUseCase

    @BeforeEach
    fun setup() {
        requestTokenStorage = InMemoryOAuthRequestTokenStorage()
        usosOAuthClient = mock()
        userRepository = InMemoryUserRepository()
        tokenStorage = InMemoryTokenStorage()
        tokenGenerator = object : TokenGenerator {
            private var i = 0
            override fun generateToken(): String = "gen-${++i}"
        }
        authProperties = AuthProperties(accessTokenExpirationSeconds = 900, refreshTokenExpirationSeconds = 3600)
        useCase = FinalizeLoginUseCase(
            requestTokenStorage,
            usosOAuthClient,
            userRepository,
            tokenStorage,
            tokenGenerator,
            authProperties
        )
    }

    @Test
    fun `executes successfully when token and verifier are valid`() = runTest {
        requestTokenStorage.storeRequestTokenSecret("oauth-1", "sec-1", Duration.ofMinutes(5))
        given(usosOAuthClient.getAccessToken("oauth-1", "ver-1", "sec-1"))
            .willReturn(UsosAccessToken("usos-acc", "usos-sec"))
        given(usosOAuthClient.getUserProfile("usos-acc", "usos-sec"))
            .willReturn(UsosUserProfile("usos-123", "Anna", "Nowak"))

        val response = useCase.execute(FinalizeLoginRequest("oauth-1", "ver-1"))
        assertTrue(response is AuthSessionResponse.Success)
        val success = response as AuthSessionResponse.Success
        assertEquals("gen-1", success.accessToken)
        assertEquals("gen-2", success.refreshToken)

        val user = userRepository.findByUsosId("usos-123")
        assertNotNull(user)
        assertEquals("Anna", user?.firstName)
        assertEquals(user?.id, success.userId)
    }

    @Test
    fun `returns InvalidOrExpiredToken if request secret is missing`() = runTest {
        val response = useCase.execute(FinalizeLoginRequest("missing-token", "ver-1"))
        assertTrue(response is AuthSessionResponse.InvalidOrExpiredToken)
    }
}
