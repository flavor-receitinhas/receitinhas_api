package com.flavor.recipes.core.security


import com.flavor.recipes.user.entities.UserEntity
import com.google.firebase.auth.AuthErrorCode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service


@Service
class SecurityService {
    var logger: Logger = LoggerFactory.getLogger("TokenService")
    fun validateToken(token: String?): UserEntity? {
        try {
            if (token == null) return null
            // Verify the ID token while checking if the token is revoked by passing checkRevoked
            // as true.
            val checkRevoked = true
            val decodedToken = FirebaseAuth.getInstance()
                .verifyIdToken(token, checkRevoked)
            // Token is valid and not revoked.
            return UserEntity(
                id = decodedToken.claims["user_id"] as String,
                email = decodedToken.claims["email"] as String,
                emailVerified = decodedToken.claims["email_verified"] as Boolean,
                signProvider = (decodedToken.claims["firebase"] as Map<*, *>)["sign_in_provider"] as String
            )
        } catch (e: FirebaseAuthException) {
            if (e.authErrorCode == AuthErrorCode.REVOKED_ID_TOKEN) {
                return null
            }
            logger.info(e.message)
            return null
        }
    }
}