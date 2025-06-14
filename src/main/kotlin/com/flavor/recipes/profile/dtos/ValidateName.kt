package com.flavor.recipes.profile.dtos

data class ValidateName(
    val name: String,
    val isValid: Boolean,
    val message: String,
)
