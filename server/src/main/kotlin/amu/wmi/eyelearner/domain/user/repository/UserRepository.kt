package amu.wmi.eyelearner.domain.user.repository

import amu.wmi.eyelearner.domain.user.model.User

interface UserRepository {
    suspend fun findById(id: String): User?
    suspend fun findByUsosId(usosId: String): User?
    suspend fun save(user: User): User
    suspend fun deleteById(id: String): Boolean
}
