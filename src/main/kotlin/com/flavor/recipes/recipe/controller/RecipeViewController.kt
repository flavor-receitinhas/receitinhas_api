package com.flavor.recipes.recipe.controller

import com.flavor.recipes.core.BusinessException
import com.flavor.recipes.core.messages.MessageService
import com.flavor.recipes.recipe.dtos.RecipeViewsConsumerDto
import com.flavor.recipes.recipe.repositories.RecipeRepository
import com.flavor.recipes.user.entities.UserEntity
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/recipe/{recipeId}/view")
@Tag(name = "Recipe")
class RecipeViewController {
    @Autowired
    lateinit var recipeRepository: RecipeRepository

    @Autowired
    lateinit var messageService: MessageService

    @PostMapping("/v1")
    fun putView(
        @PathVariable recipeId: String,
        @AuthenticationPrincipal userAuth: UserEntity
    ) {
        val recipe = recipeRepository.findById(recipeId).getOrNull()
            ?: throw BusinessException("Receita não encontrado: $recipeId")
        messageService.sendViewManga(
            RecipeViewsConsumerDto(
                recipeId = recipe.id!!,
                userId = userAuth.id
            )
        )
    }
}