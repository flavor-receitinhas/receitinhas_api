package com.flavor.recipes.core.services

import com.flavor.recipes.core.BusinessException
import com.flavor.recipes.dash.entities.RoleType
import com.flavor.recipes.dash.repositories.RoleRepository
import com.flavor.recipes.user.entities.UserEntity
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class HandlerRoleUser(val repository: RoleRepository) {
    fun handleIsAdmin(userAuth: UserEntity) {
        val result = repository.findByUserId(userAuth.id)
        if (result.getOrNull()?.type != RoleType.admin) {
            throw BusinessException("O usuario não tem permissão")
        }
    }

    //Verificar se o usuario do token é o mesmo da url, caso for admin ele faz oque quiser
    fun handleIsOwnerToken(userAuth: UserEntity, userId: String) {
        if (userId == userAuth.id) {
            return
        }
        handleIsAdmin(userAuth)
    }
}