package amu.wmi.eyelearner.auth.controller

import amu.wmi.eyelearner.auth.AuthEndpoints
import amu.wmi.eyelearner.auth.dto.FinalizeLoginRequest
import amu.wmi.eyelearner.auth.dto.InitLoginRequest
import amu.wmi.eyelearner.auth.dto.LogoutRequest
import amu.wmi.eyelearner.auth.dto.RefreshSessionRequest
import amu.wmi.eyelearner.domain.token.repository.TokenStorage
import amu.wmi.eyelearner.domain.user.model.User
import amu.wmi.eyelearner.domain.user.repository.UserRepository
import amu.wmi.eyelearner.infrastructure.usos.UsosAccessToken
import amu.wmi.eyelearner.infrastructure.usos.UsosApiException
import amu.wmi.eyelearner.infrastructure.usos.UsosOAuthClient
import amu.wmi.eyelearner.infrastructure.usos.UsosRequestToken
import amu.wmi.eyelearner.infrastructure.usos.UsosUserProfile
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Duration

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerIntegrationTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockitoBean
    private lateinit var usosOAuthClient: UsosOAuthClient

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var tokenStorage: TokenStorage

    @BeforeEach
    fun setup() {
        // Clear repositories before each test
        (userRepository as? amu.wmi.eyelearner.infrastructure.persistence.InMemoryUserRepository)?.clear()
        (tokenStorage as? amu.wmi.eyelearner.infrastructure.persistence.InMemoryTokenStorage)?.clear()
    }

    @Test
    fun `POST login init returns 200 on success`() {
        given(kotlinx.coroutines.runBlocking {
            usosOAuthClient.getRequestToken("https://myapp.com/callback", null)
        }).willReturn(
            UsosRequestToken("req-token-123", "req-sec-456", true)
        )

        webTestClient.post()
            .uri(AuthEndpoints.LOGIN_INIT)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(InitLoginRequest("https://myapp.com/callback"))
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.oauthToken").isEqualTo("req-token-123")
            .jsonPath("$.authorizeUrl").isNotEmpty
    }

    @Test
    fun `POST login init returns 502 Bad Gateway when USOS fails`() {
        given(kotlinx.coroutines.runBlocking {
            usosOAuthClient.getRequestToken("https://myapp.com/callback", null)
        }).willAnswer { throw UsosApiException("USOS Gateway Timeout", 504) }

        webTestClient.post()
            .uri(AuthEndpoints.LOGIN_INIT)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(InitLoginRequest("https://myapp.com/callback"))
            .exchange()
            .expectStatus().isEqualTo(502)
    }

    @Test
    fun `POST login finalize returns 200 with tokens on success`() {
        kotlinx.coroutines.runBlocking {
            tokenStorage.storeRequestTokenSecret("req-token-123", "req-sec-456", Duration.ofMinutes(15))
            given(usosOAuthClient.getAccessToken("req-token-123", "verifier-789", "req-sec-456"))
                .willReturn(UsosAccessToken("usos-acc-1", "usos-sec-1"))
            given(usosOAuthClient.getUserProfile("usos-acc-1", "usos-sec-1"))
                .willReturn(UsosUserProfile("usos-user-99", "Jan", "Kowalski"))
        }

        webTestClient.post()
            .uri(AuthEndpoints.LOGIN_FINALIZE)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(FinalizeLoginRequest("req-token-123", "verifier-789"))
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.accessToken").isNotEmpty
            .jsonPath("$.refreshToken").isNotEmpty
            .jsonPath("$.expiresInSeconds").isEqualTo(900)
    }

    @Test
    fun `POST login finalize returns 401 Unauthorized for invalid or expired token`() {
        webTestClient.post()
            .uri(AuthEndpoints.LOGIN_FINALIZE)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(FinalizeLoginRequest("expired-token", "some-verifier"))
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `POST refresh returns 200 with new tokens on success`() {
        kotlinx.coroutines.runBlocking {
            val user = User("u-1", "usos-1", "Jan", "Kowalski")
            userRepository.save(user)
            tokenStorage.storeAccessToken("active-acc", "u-1", Duration.ofMinutes(15))
            tokenStorage.storeRefreshToken("active-ref", "u-1", "active-acc", Duration.ofDays(7))
        }

        webTestClient.post()
            .uri(AuthEndpoints.REFRESH)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(RefreshSessionRequest("active-ref"))
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.accessToken").isNotEmpty
            .jsonPath("$.refreshToken").isNotEmpty
    }

    @Test
    fun `POST refresh returns 401 Unauthorized for unknown refresh token`() {
        webTestClient.post()
            .uri(AuthEndpoints.REFRESH)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(RefreshSessionRequest("unknown-ref"))
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `POST logout returns 200 on success`() {
        kotlinx.coroutines.runBlocking {
            tokenStorage.storeAccessToken("acc-to-logout", "u-1", Duration.ofMinutes(15))
            tokenStorage.storeRefreshToken("ref-to-logout", "u-1", "acc-to-logout", Duration.ofDays(7))
        }

        webTestClient.post()
            .uri(AuthEndpoints.LOGOUT)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(LogoutRequest("ref-to-logout"))
            .exchange()
            .expectStatus().isOk

        // Verify tokens revoked
        val acc = kotlinx.coroutines.runBlocking { tokenStorage.findAccessToken("acc-to-logout") }
        val ref = kotlinx.coroutines.runBlocking { tokenStorage.findRefreshToken("ref-to-logout") }
        org.junit.jupiter.api.Assertions.assertNull(acc)
        org.junit.jupiter.api.Assertions.assertNull(ref)
    }
}
