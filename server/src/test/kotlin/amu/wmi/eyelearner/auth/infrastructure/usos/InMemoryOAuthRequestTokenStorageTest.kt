package amu.wmi.eyelearner.auth.infrastructure.usos

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration

class InMemoryOAuthRequestTokenStorageTest {

    private lateinit var storage: InMemoryOAuthRequestTokenStorage

    @BeforeEach
    fun setup() {
        storage = InMemoryOAuthRequestTokenStorage()
    }

    @Test
    fun `requestTokenSecret temporary storage is single-use`() = runTest {
        storage.storeRequestTokenSecret("oauth-token-1", "secret-1", Duration.ofMinutes(10))

        val firstGet = storage.getAndRemoveRequestTokenSecret("oauth-token-1")
        assertEquals("secret-1", firstGet)

        val secondGet = storage.getAndRemoveRequestTokenSecret("oauth-token-1")
        assertNull(secondGet)
    }

    @Test
    fun `expired requestTokenSecret returns null`() = runTest {
        storage.storeRequestTokenSecret("expired-oauth", "secret-2", Duration.ofSeconds(-1))

        val secret = storage.getAndRemoveRequestTokenSecret("expired-oauth")
        assertNull(secret)
    }
}
