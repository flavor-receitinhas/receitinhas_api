package com.flavor.recipes.user.entities


import com.flavor.recipes.dash.entities.RoleType
import java.sql.Timestamp

data class UserEntity(
    val id: String,
    val email: String,
    val emailVerified: Boolean,
    val signProvider: String,
    var createdAt: Timestamp,
    val disabled: Boolean,
    val type: RoleType
)