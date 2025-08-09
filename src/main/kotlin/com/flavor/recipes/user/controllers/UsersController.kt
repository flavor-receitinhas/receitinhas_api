package com.flavor.recipes.user.controllers

import com.flavor.recipes.core.services.HandlerRoleUser
import com.flavor.recipes.user.entities.UserEntity
import com.flavor.recipes.user.services.UsersService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
@Tag(name = "User")
class UsersController(var usersService: UsersService, val handlerRoleUser: HandlerRoleUser) {
    @GetMapping
    fun list(@AuthenticationPrincipal userAuth: UserEntity, pageToken: String?): List<UserEntity> {
        handlerRoleUser.handleIsAdmin(userAuth)
        return usersService.list(pageToken)
    }

    @PostMapping("/disable/{userId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun blockUser(@AuthenticationPrincipal userAuth: UserEntity, @PathVariable userId: String) {
        handlerRoleUser.handleIsAdmin(userAuth)
        usersService.disableUser(userId, true)
    }

    @PostMapping("/enable/{userId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun enable(@AuthenticationPrincipal userAuth: UserEntity, @PathVariable userId: String) {
        handlerRoleUser.handleIsAdmin(userAuth)
        usersService.disableUser(userId, false)
    }
}