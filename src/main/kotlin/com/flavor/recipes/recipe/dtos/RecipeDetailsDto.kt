package com.flavor.recipes.recipe.dtos

import com.flavor.recipes.recipe.entities.RecipeImageEntity

data class RecipeDetailsDto(
    val isFavorite: Boolean = false,
    val recipe: RecipeGetDto,
    val images: List<RecipeImageEntity>,
    val ingredients: List<RecipeIngredientListDto>
)
