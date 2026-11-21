package amu.wmi.eyelearner.auth.infrastructure.token

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration

class InMemoryTokenStorageTest {

    private lateinit var storage: InMemoryTokenStorage

    @BeforeEach
    fun setup() {
        storage = InMemoryTokenStorage()
    }

    @Test
    fun `storeAccessToken and findAccessToken works`() = runTest {
        storage.storeAccessToken("token-abc", "user-1", Duration.ofMinutes(15))

        val record = storage.findAccessToken("token-abc")
        assertNotNull(record)
        assertEquals("user-1", record?.userId)
        assertFalse(record?.isExpired() ?: true)
    }

    @Test
    fun `expired access token returns null`() = runTest {
        storage.storeAccessToken("expired-token", "user-1", Duration.ofSeconds(-1))

        val record = storage.findAccessToken("expired-token")
        assertNull(record)
    }

    @Test
    fun `revokeAccessToken deletes the token`() = runTest {
        storage.storeAccessToken("token-123", "user-1", Duration.ofMinutes(15))
        storage.revokeAccessToken("token-123")

        assertNull(storage.findAccessToken("token-123"))
    }

    @Test
    fun `storeRefreshToken and findRefreshToken works`() = runTest {
        storage.storeRefreshToken("refresh-abc", "user-1", "access-abc", Duration.ofDays(7))

        val record = storage.findRefreshToken("refresh-abc")
        assertNotNull(record)
        assertEquals("user-1", record?.userId)
        assertEquals("access-abc", record?.associatedAccessToken)
    }

    @Test
    fun `expired refresh token returns null`() = runTest {
        storage.storeRefreshToken("expired-refresh", "user-1", null, Duration.ofSeconds(-1))

        val record = storage.findRefreshToken("expired-refresh")
        assertNull(record)
    }

    @Test
    fun `revokeRefreshToken deletes the refresh token`() = runTest {
        storage.storeRefreshToken("refresh-123", "user-1", null, Duration.ofDays(7))
        storage.revokeRefreshToken("refresh-123")

        assertNull(storage.findRefreshToken("refresh-123"))
    }
}
