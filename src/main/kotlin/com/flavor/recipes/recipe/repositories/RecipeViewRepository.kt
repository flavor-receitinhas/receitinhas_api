package com.flavor.recipes.recipe.repositories

import com.flavor.recipes.recipe.entities.RecipeViewEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface RecipeViewRepository : JpaRepository<RecipeViewEntity, Long> {
    fun findByRecipeIdAndUserId(
        recipeId: String,
        userId: String
    ): Optional<RecipeViewEntity>

    @Query(
        """
            SELECT COUNT(*)
            FROM RecipeViewEntity dc 
            WHERE dc.recipeId = :recipeId
        """
    )
    fun countByRecipeId(@Param("recipeId") recipeId: String): Long

}