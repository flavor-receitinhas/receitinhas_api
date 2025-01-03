package com.flavor.recipes.recipe.entities

import jakarta.persistence.*

@Entity
@Table(name = "recipe_view")
data class RecipeViewEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false, name = "recipe_id")
    val recipeId: String = "",
    @Column(name = "user_id", nullable = false)
    val userId: String = "",
    @Column(name = "created_at", nullable = false)
    val createdAt: Long = 0,
)