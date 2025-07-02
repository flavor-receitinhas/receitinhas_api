package com.flavor.recipes.recipe.dtos

import com.flavor.recipes.recipe.entities.RecipeEntity

data class RecipeDetailsDto(
    val isFavorite: Boolean = false,
    val recipe: RecipeEntity,
    val images: List<RecipeIngredientListDto>,
    val ingredients: List<RecipeIngredientListDto>
)
