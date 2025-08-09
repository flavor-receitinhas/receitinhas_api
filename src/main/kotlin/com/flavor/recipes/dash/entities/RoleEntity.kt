package com.flavor.recipes.dash.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.UuidGenerator

@Entity
@Table(name = "role")
data class RoleEntity(
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, unique = true, nullable = false)
    val id: String? = null,
    @Column(nullable = false, unique = true)
    val userId: String,
    @Column(nullable = false)
    val type: RoleType
)
