package com.flavor.recipes.dash.controllers

import com.flavor.recipes.core.BusinessException
import com.flavor.recipes.core.services.HandlerRoleUser
import com.flavor.recipes.dash.entities.CreateRoleUserDto
import com.flavor.recipes.dash.entities.RoleEntity
import com.flavor.recipes.dash.repositories.RoleRepository
import com.flavor.recipes.user.entities.UserEntity
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/role")
@Tag(name = "Roles")
class RoleController(val roleRepository: RoleRepository, val handlerRoleUser: HandlerRoleUser) {
    @GetMapping("/me")
    fun get(@AuthenticationPrincipal userAuth: UserEntity): RoleEntity? {
        val result = roleRepository.findByUserId(userAuth.id)
        if (result.isPresent)
            return result.get()
        throw BusinessException("Usuario sem role")
    }

    @PostMapping
    fun create(@AuthenticationPrincipal userAuth: UserEntity, @RequestBody body: CreateRoleUserDto): RoleEntity? {
        handlerRoleUser.handleIsAdmin(userAuth)
        val findRole = roleRepository.findByUserId(body.userId)
        if (findRole.isPresent) {
            throw BusinessException("Usuario já tem role")
        }
        val result = roleRepository.save(
            RoleEntity(
                userId = body.userId,
                type = body.type,
            )
        )
        return result
    }

    @PutMapping("/{id}")
    fun update(
        @AuthenticationPrincipal userAuth: UserEntity,
        @RequestBody body: CreateRoleUserDto,
        @PathVariable id: String
    ): RoleEntity? {
        handlerRoleUser.handleIsAdmin(userAuth)
        val findRole = roleRepository.findById(id)
        if (!findRole.isPresent) {
            throw BusinessException("Role não encontrada")
        }
        return roleRepository.save(
            findRole.get().copy(type = body.type)
        )
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun delete(
        @AuthenticationPrincipal userAuth: UserEntity,
        @PathVariable id: String
    ) {
        handlerRoleUser.handleIsAdmin(userAuth)
        val findRole = roleRepository.findById(id)
        if (!findRole.isPresent) {
            throw BusinessException("Role não encontrada")
        }
        roleRepository.deleteById(id)
    }
}