package com.flavor.recipes.dash.entities

data class UpdateRoleUserDto(
    val userId: String = "",
    val type: RoleType = RoleType.common
)
