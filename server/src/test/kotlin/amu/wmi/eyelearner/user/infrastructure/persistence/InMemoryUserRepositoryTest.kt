package amu.wmi.eyelearner.user.infrastructure.persistence

import amu.wmi.eyelearner.user.domain.model.User
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

class InMemoryUserRepositoryTest {

    private lateinit var repository: InMemoryUserRepository

    @BeforeEach
    fun setup() {
        repository = InMemoryUserRepository()
    }

    @Test
    fun `save and findById returns correct user`() = runTest {
        val user = User(
            id = "user-1",
            usosId = "usos-100",
            firstName = "Jan",
            lastName = "Kowalski",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        repository.save(user)

        val found = repository.findById("user-1")
        assertNotNull(found)
        assertEquals("Jan", found?.firstName)
        assertEquals("usos-100", found?.usosId)
    }

    @Test
    fun `findByUsosId returns correct user`() = runTest {
        val user = User(
            id = "user-2",
            usosId = "usos-200",
            firstName = "Anna",
            lastName = "Nowak"
        )
        repository.save(user)

        val found = repository.findByUsosId("usos-200")
        assertNotNull(found)
        assertEquals("user-2", found?.id)
        assertEquals("Anna", found?.firstName)
    }

    @Test
    fun `update existing user modifies data`() = runTest {
        val user = User(
            id = "user-3",
            usosId = "usos-300",
            firstName = "Piotr",
            lastName = "Zieliński"
        )
        repository.save(user)

        val updated = user.copy(firstName = "Piotr Jan")
        repository.save(updated)

        val found = repository.findById("user-3")
        assertEquals("Piotr Jan", found?.firstName)
    }

    @Test
    fun `deleteById removes user from both indices`() = runTest {
        val user = User(
            id = "user-4",
            usosId = "usos-400",
            firstName = "Marek",
            lastName = "Lewandowski"
        )
        repository.save(user)

        val deleted = repository.deleteById("user-4")
        assertTrue(deleted)
        assertNull(repository.findById("user-4"))
        assertNull(repository.findByUsosId("usos-400"))
    }
}
