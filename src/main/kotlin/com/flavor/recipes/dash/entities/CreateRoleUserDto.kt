package com.flavor.recipes.dash.entities

data class CreateRoleUserDto(
    val userId: String = "",
    val type: RoleType = RoleType.common
)
