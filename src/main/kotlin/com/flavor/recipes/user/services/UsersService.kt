package com.flavor.recipes.user.services

import com.flavor.recipes.user.entities.UserEntity
import com.google.firebase.auth.FirebaseAuth
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
                createdAt = Timestamp.from(Instant.ofEpochMilli(it.userMetadata.creationTimestamp))
            )
        }
    }

    fun disableUser(userId: String, disabled: Boolean) {
        val request = UserRecord.UpdateRequest(userId)
            .setDisabled(disabled)
        FirebaseAuth.getInstance().updateUser(request)
    }
}