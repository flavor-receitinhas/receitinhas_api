package com.flavor.recipes.dash.controllers

import com.flavor.recipes.core.BusinessException
import com.flavor.recipes.dash.entities.RoleEntity
import com.flavor.recipes.dash.repositories.RoleRepository
import com.flavor.recipes.user.entities.UserEntity
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/role")
@Tag(name = "Roles")
class RoleController(val roleRepository: RoleRepository) {
    @GetMapping("/me")
    fun get(@AuthenticationPrincipal userAuth: UserEntity): RoleEntity? {
        val result = roleRepository.findByUserId(userAuth.id)
        if (result.isPresent)
            return result.get()
        throw BusinessException("Usuario sem role")
    }
}