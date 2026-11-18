package amu.wmi.eyelearner.infrastructure.persistence

import amu.wmi.eyelearner.domain.user.model.User
import amu.wmi.eyelearner.domain.user.repository.UserRepository
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

@Repository
class InMemoryUserRepository : UserRepository {

    private val usersById = ConcurrentHashMap<String, User>()
    private val userIdsByUsosId = ConcurrentHashMap<String, String>()

    override suspend fun findById(id: String): User? {
        return usersById[id]
    }

    override suspend fun findByUsosId(usosId: String): User? {
        val id = userIdsByUsosId[usosId] ?: return null
        return usersById[id]
    }

    override suspend fun save(user: User): User {
        usersById[user.id] = user
        userIdsByUsosId[user.usosId] = user.id
        return user
    }

    override suspend fun deleteById(id: String): Boolean {
        val user = usersById.remove(id) ?: return false
        userIdsByUsosId.remove(user.usosId)
        return true
    }

    fun clear() {
        usersById.clear()
        userIdsByUsosId.clear()
    }
}
