package com.flavor.recipes.dash.controllers

import com.flavor.recipes.core.BusinessException
import com.flavor.recipes.core.services.HandlerRoleUser
import com.flavor.recipes.dash.entities.RoleEntity
import com.flavor.recipes.dash.entities.RoleType
import com.flavor.recipes.dash.entities.UpdateRoleUserDto
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
    fun getMe(@AuthenticationPrincipal userAuth: UserEntity): RoleEntity? {
        val result = roleRepository.findByUserId(userAuth.id)
        if (result.isPresent)
            return result.get()
        throw BusinessException("Usuario sem role")
    }

    @GetMapping
    fun get(): List<RoleType> {
        return RoleType.values().toList()
    }

    @PutMapping("/user")
    fun update(
        @AuthenticationPrincipal userAuth: UserEntity,
        @RequestBody body: UpdateRoleUserDto,
    ): RoleEntity? {
        handlerRoleUser.handleIsAdmin(userAuth)
        val findRole = roleRepository.findByUserId(body.userId)
        return if (findRole.isPresent) {
            roleRepository.save(
                findRole.get().copy(type = body.type)
            )
        } else {
            roleRepository.save(
                RoleEntity(
                    userId = body.userId,
                    type = body.type,
                )
            )
        }
    }

    @DeleteMapping("/user/{id}")
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