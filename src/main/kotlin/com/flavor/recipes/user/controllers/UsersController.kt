package com.flavor.recipes.user.controllers

import com.flavor.recipes.user.entities.UserEntity
import com.flavor.recipes.user.services.UsersService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
@Tag(name = "User")
class UsersController(var usersService: UsersService) {
    @GetMapping
    fun list(pageToken: String?): List<UserEntity> {
        return usersService.list(pageToken)
    }
}