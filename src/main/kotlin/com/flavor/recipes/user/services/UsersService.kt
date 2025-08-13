package com.flavor.recipes.user.services

import com.flavor.recipes.core.BusinessException
import com.flavor.recipes.core.NotFoundException
import com.flavor.recipes.dash.entities.RoleType
import com.flavor.recipes.dash.repositories.RoleRepository
import com.flavor.recipes.user.entities.UserEntity
import com.google.firebase.ErrorCode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserRecord
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant
import kotlin.jvm.optionals.getOrNull

@Service
class UsersService(val roleRepository: RoleRepository) {
    fun list(pageToken: String?): List<UserEntity> {
        val result = FirebaseAuth.getInstance().listUsers(pageToken, 25)
        return result.values.map {
            val role = roleRepository.findByUserId(it.uid)
            UserEntity(
                id = it.uid,
                email = it.email,
                emailVerified = it.isEmailVerified,
                signProvider = it.providerId,
                createdAt = Timestamp.from(Instant.ofEpochMilli(it.userMetadata.creationTimestamp)),
                disabled = it.isDisabled,
                type = role.getOrNull()?.type ?: RoleType.common
            )
        }
    }

    fun getById(uid: String): UserEntity {
        try {
            val user = FirebaseAuth.getInstance().getUser(uid)
            val role = roleRepository.findByUserId(uid)
            return UserEntity(
                id = user.uid,
                email = user.email,
                emailVerified = user.isEmailVerified,
                signProvider = user.providerId,
                createdAt = Timestamp.from(Instant.ofEpochMilli(user.userMetadata.creationTimestamp)),
                disabled = user.isDisabled,
                type = role.getOrNull()?.type ?: RoleType.common
            )
        } catch (e: FirebaseAuthException) {
            if (e.errorCode == ErrorCode.NOT_FOUND) {
                throw NotFoundException(e.message ?: e.toString())
            }
            throw BusinessException(e.message ?: e.toString(), e.errorCode.name)
        }
    }

    fun getByEmail(email: String): UserEntity {
        try {
            val user = FirebaseAuth.getInstance().getUserByEmail(email)
            val role = roleRepository.findByUserId(user.uid)
            return UserEntity(
                id = user.uid,
                email = user.email,
                emailVerified = user.isEmailVerified,
                signProvider = user.providerId,
                createdAt = Timestamp.from(Instant.ofEpochMilli(user.userMetadata.creationTimestamp)),
                disabled = user.isDisabled,
                type = role.getOrNull()?.type ?: RoleType.common
            )
        } catch (e: FirebaseAuthException) {
            if (e.errorCode == ErrorCode.NOT_FOUND) {
                throw NotFoundException(e.message ?: e.toString())
            }
            throw BusinessException(e.message ?: e.toString(), e.errorCode.name)
        }
    }

    fun disableUser(userId: String, disabled: Boolean) {
        val request = UserRecord.UpdateRequest(userId)
            .setDisabled(disabled)
        FirebaseAuth.getInstance().updateUser(request)
    }
}