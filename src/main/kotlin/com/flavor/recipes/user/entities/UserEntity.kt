package com.flavor.recipes.user.entities

import java.sql.Timestamp

data class UserEntity(
    val id: String,
    val email: String,
    val emailVerified: Boolean,
    val signProvider: String,
    var createdAt: Timestamp,
)