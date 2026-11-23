package amu.wmi.eyelearner.auth.application.usecase

import amu.wmi.eyelearner.auth.dto.LogoutRequest
import amu.wmi.eyelearner.auth.dto.LogoutResponse
import amu.wmi.eyelearner.auth.infrastructure.token.InMemoryTokenStorage
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration

class LogoutUseCaseTest {

    private lateinit var tokenStorage: InMemoryTokenStorage
    private lateinit var useCase: LogoutUseCase

    @BeforeEach
    fun setup() {
        tokenStorage = InMemoryTokenStorage()
        useCase = LogoutUseCase(tokenStorage)
    }

    @Test
    fun `revokes refresh token and associated access token`() = runTest {
        tokenStorage.storeAccessToken("acc-1", "user-1", Duration.ofMinutes(15))
        tokenStorage.storeRefreshToken("ref-1", "user-1", "acc-1", Duration.ofDays(7))

        val response = useCase.execute(LogoutRequest("ref-1"))
        assertEquals(LogoutResponse.Success, response)

        assertNull(tokenStorage.findAccessToken("acc-1"))
        assertNull(tokenStorage.findRefreshToken("ref-1"))
    }

    @Test
    fun `returns Success even if token already non-existent`() = runTest {
        val response = useCase.execute(LogoutRequest("unknown-ref"))
        assertEquals(LogoutResponse.Success, response)
    }
}
