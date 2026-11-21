package amu.wmi.eyelearner.auth.application.usecase

import amu.wmi.eyelearner.auth.domain.usos.exception.UsosApiException
import amu.wmi.eyelearner.auth.domain.usos.model.UsosRequestToken
import amu.wmi.eyelearner.auth.domain.usos.service.UsosOAuthClient
import amu.wmi.eyelearner.auth.dto.InitLoginRequest
import amu.wmi.eyelearner.auth.dto.InitLoginResponse
import amu.wmi.eyelearner.auth.infrastructure.config.UsosProperties
import amu.wmi.eyelearner.auth.infrastructure.usos.InMemoryOAuthRequestTokenStorage
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock

class InitiateLoginUseCaseTest {

    private lateinit var usosOAuthClient: UsosOAuthClient
    private lateinit var requestTokenStorage: InMemoryOAuthRequestTokenStorage
    private lateinit var usosProperties: UsosProperties
    private lateinit var useCase: InitiateLoginUseCase

    @BeforeEach
    fun setup() {
        usosOAuthClient = mock()
        requestTokenStorage = InMemoryOAuthRequestTokenStorage()
        usosProperties = UsosProperties(
            baseUrl = "https://usosapps.amu.edu.pl",
            consumerKey = "key",
            consumerSecret = "secret"
        )
        useCase = InitiateLoginUseCase(usosOAuthClient, requestTokenStorage, usosProperties)
    }

    @Test
    fun `executes successfully and stores request token secret`() = runTest {
        given(usosOAuthClient.getRequestToken("https://example.com/cb", null))
            .willReturn(UsosRequestToken("req-tok", "req-sec", true))

        val response = useCase.execute(InitLoginRequest("https://example.com/cb"))
        assertTrue(response is InitLoginResponse.Success)
        val success = response as InitLoginResponse.Success
        assertEquals("req-tok", success.oauthToken)
        assertEquals("https://usosapps.amu.edu.pl/services/oauth/authorize?oauth_token=req-tok", success.authorizeUrl)

        val stored = requestTokenStorage.getAndRemoveRequestTokenSecret("req-tok")
        assertEquals("req-sec", stored)
    }

    @Test
    fun `returns error when USOS fails`() = runTest {
        given(usosOAuthClient.getRequestToken("https://example.com/cb", null))
            .willAnswer { throw UsosApiException("API down", 502) }

        val response = useCase.execute(InitLoginRequest("https://example.com/cb"))
        assertTrue(response is InitLoginResponse.Error)
        val error = response as InitLoginResponse.Error
        assertTrue(error.message.contains("API down"))
    }
}
