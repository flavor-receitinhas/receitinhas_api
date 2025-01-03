package com.flavor.recipes.recipe.consumers


import com.flavor.recipes.core.messages.MessageName
import com.flavor.recipes.recipe.dtos.RecipeViewsConsumerDto
import com.flavor.recipes.recipe.entities.RecipeViewEntity
import com.flavor.recipes.recipe.repositories.RecipeRepository
import com.flavor.recipes.recipe.repositories.RecipeViewRepository
import com.github.sonus21.rqueue.annotation.RqueueListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class RecipeViewConsumer {
    @Autowired
    lateinit var recipeViewRepository: RecipeViewRepository

    @Autowired
    lateinit var recipeRepository: RecipeRepository

    @RqueueListener(MessageName.RECIPE_VIEW, numRetries = "1", concurrency = "1")
    fun onMessage(view: RecipeViewsConsumerDto) {
        val result = recipeViewRepository.findByRecipeIdAndUserId(view.recipeId, view.userId)
        if (!result.isPresent) {
            recipeViewRepository.save(
                RecipeViewEntity(
                    userId = view.userId,
                    recipeId = view.recipeId,
                    createdAt = Date().time,
                )
            )
            val totalView = recipeViewRepository.countByRecipeId(view.recipeId)
            val recipe = recipeRepository.findById(view.recipeId)
            recipeRepository.save(recipe.get().copy(totalViews = totalView))
        }
    }
}