package com.flavor.recipes.recipe.dtos

data class  RecipeIngredientListDto(
    val id: String? = null,
    val ingredientId: String,
    val ingredientName: String,
    val recipeId: String,
    val quantity: Double,
    val unit: String
)