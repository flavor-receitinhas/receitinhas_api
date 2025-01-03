package com.flavor.recipes.core.messages

import com.flavor.recipes.recipe.dtos.RecipeViewsConsumerDto
import com.github.sonus21.rqueue.core.RqueueMessageEnqueuer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class MessageService {
    @Autowired
    lateinit var rqueueMessageEnqueuer: RqueueMessageEnqueuer

    fun sendViewManga(view: RecipeViewsConsumerDto) {
        rqueueMessageEnqueuer.enqueue(MessageName.RECIPE_VIEW, view)
    }
}