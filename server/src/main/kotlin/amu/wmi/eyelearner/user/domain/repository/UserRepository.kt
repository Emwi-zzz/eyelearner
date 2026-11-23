package amu.wmi.eyelearner.user.domain.repository

import amu.wmi.eyelearner.user.domain.model.User

interface UserRepository {
    suspend fun findById(id: String): User?
    suspend fun findByUsosId(usosId: String): User?
    suspend fun save(user: User): User
    suspend fun deleteById(id: String): Boolean
}
