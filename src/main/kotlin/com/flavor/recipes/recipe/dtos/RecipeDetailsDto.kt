package com.flavor.recipes.recipe.dtos

import com.flavor.recipes.favorite.dtos.FavoriteCheckDto
import com.flavor.recipes.recipe.entities.RecipeImageEntity

data class RecipeDetailsDto(
    val favorite: FavoriteCheckDto,
    val recipe: RecipeGetDto,
    val images: List<RecipeImageEntity>,
    val ingredients: List<RecipeIngredientListDto>
)
