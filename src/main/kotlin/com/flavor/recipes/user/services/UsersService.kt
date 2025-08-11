package com.flavor.recipes.user.services

import com.flavor.recipes.core.BusinessException
import com.flavor.recipes.core.NotFoundException
import com.flavor.recipes.user.entities.UserEntity
import com.google.firebase.ErrorCode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserRecord
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant

@Service
class UsersService {
    fun list(pageToken: String?): List<UserEntity> {
        val result = FirebaseAuth.getInstance().listUsers(pageToken, 25)
        return result.values.map {
            UserEntity(
                id = it.uid,
                email = it.email,
                emailVerified = it.isEmailVerified,
                signProvider = it.providerId,
                createdAt = Timestamp.from(Instant.ofEpochMilli(it.userMetadata.creationTimestamp)),
                disabled = it.isDisabled
            )
        }
    }

    fun getById(uid: String): UserEntity {
        try {
            val user = FirebaseAuth.getInstance().getUser(uid)
            return UserEntity(
                id = user.uid,
                email = user.email,
                emailVerified = user.isEmailVerified,
                signProvider = user.providerId,
                createdAt = Timestamp.from(Instant.ofEpochMilli(user.userMetadata.creationTimestamp)),
                disabled = user.isDisabled
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
            return UserEntity(
                id = user.uid,
                email = user.email,
                emailVerified = user.isEmailVerified,
                signProvider = user.providerId,
                createdAt = Timestamp.from(Instant.ofEpochMilli(user.userMetadata.creationTimestamp)),
                disabled = user.isDisabled
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