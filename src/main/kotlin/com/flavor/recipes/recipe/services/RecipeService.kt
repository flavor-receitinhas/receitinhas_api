package com.flavor.recipes.recipe.services

import com.flavor.recipes.core.BusinessException
import com.flavor.recipes.ingredient.repository.IngredientRepository
import com.flavor.recipes.profile.repositories.ProfileRepository
import com.flavor.recipes.profile.services.ProfileService
import com.flavor.recipes.recipe.dtos.*
import com.flavor.recipes.recipe.entities.RecipeEntity
import com.flavor.recipes.recipe.entities.RecipeImageEntity
import com.flavor.recipes.recipe.entities.RecipeIngredientEntity
import com.flavor.recipes.recipe.entities.RecipeStatus
import com.flavor.recipes.recipe.repositories.RecipeBucketRepository
import com.flavor.recipes.recipe.repositories.RecipeImageRepository
import com.flavor.recipes.recipe.repositories.RecipeIngredientRepository
import com.flavor.recipes.recipe.repositories.RecipeRepository
import com.flavor.recipes.user.entities.DifficultyRecipes
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.sql.Timestamp
import java.util.Date
import kotlin.jvm.optionals.getOrNull

@Service
class RecipeService {
    @Autowired
    lateinit var recipeImageRepository: RecipeImageRepository

    @Autowired
    lateinit var recipeBucketRepository: RecipeBucketRepository

    @Autowired
    lateinit var recipeRepository: RecipeRepository

    @Autowired
    lateinit var recipeIngredientRepository: RecipeIngredientRepository

    @Autowired
    lateinit var ingredientRepository: IngredientRepository

    @Autowired
    lateinit var profileRepository: ProfileRepository
    fun search(
        isDesc: Boolean,
        page: Int,
        sort: String,
        timePreparedTo: Int? = null,
        timePreparedFrom: Int? = null,
        portionTo: Int? = null,
        portionFrom: Int? = null,
        difficultyRecipe: DifficultyRecipes? = null,
        search: String? = null,
        userId: String? = null
    ): List<RecipeListDto> {
        val result = recipeRepository.findAll(
            Specification(fun(
                root: Root<RecipeEntity>,
                _: CriteriaQuery<*>?,
                builder: CriteriaBuilder,
            ): Predicate? {
                val predicates = mutableListOf<Predicate>()
                if (timePreparedFrom != null && timePreparedTo != null) {
                    predicates.add(
                        builder.between(
                            root.get(RecipeEntity::timePrepared.name),
                            timePreparedFrom,
                            timePreparedTo
                        )
                    )
                }
                if (portionTo != null && portionFrom != null) {
                    predicates.add(
                        builder.between(
                            root.get(RecipeEntity::portion.name),
                            portionFrom,
                            portionTo
                        )
                    )
                }
                if (difficultyRecipe != null) {
                    predicates.add(
                        builder.equal(
                            root.get<String>(RecipeEntity::difficultyRecipe.name),
                            difficultyRecipe
                        )
                    )
                }
                if (userId != null) {
                    predicates.add(
                        builder.equal(
                            root.get<String>(RecipeEntity::userId.name),
                            userId
                        )
                    )
                }
                if (search != null) {
                    predicates.add(
                        builder.like(
                            builder.lower(root.get(RecipeEntity::title.name)),
                            "%" + search.lowercase() + "%"
                        )
                    )
                }
                return builder.and(*predicates.toTypedArray())
            }),
            PageRequest.of(
                page,
                25,
                if (isDesc) Sort.by(sort).descending() else
                    Sort.by(sort)
            )
        )
        return result.content.map {
            val thumb = recipeImageRepository.findByRecipeIdAndThumb(it.id!!, true).getOrNull()
            RecipeListDto(
                recipeId = it.id,
                thumb = thumb?.link,
                title = it.title,
                timePrepared = it.timePrepared
            )
        }
    }


    fun findByStatusNot(
        status: String,
        page: Int,
        isDesc: Boolean,
        sort: String
    ): List<RecipeEntity> {
        val size = 25
        val pagination = PageRequest.of(
            page,
            size,
            if (isDesc) Sort.by(sort).descending() else
                Sort.by(sort)
        )
        return recipeRepository.findByStatusNot(status, pagination)
    }

    fun findById(id: String): RecipeEntity? {
        return recipeRepository.findById(id).getOrNull()
    }

    fun findByIdWithProfile(recipeId: String): RecipeGetDto? {
        val response = recipeRepository.findById(recipeId).getOrNull()
            ?: return null
        val profile = profileRepository.findByUserId(response.userId)
        return RecipeGetDto(recipe = response, authorName = profile?.name)
    }

    fun findAllImages(recipeId: String): List<RecipeImageEntity> {
        return recipeImageRepository.findByRecipeId(recipeId)
    }

    fun countByUser(userId: String): Int {
        return recipeRepository.countByUser(userId)
    }

    fun create(dto: RecipeCreateDto, userId: String): RecipeEntity {
        return recipeRepository.save(
            RecipeEntity(
                details = dto.details,
                difficultyRecipe = dto.difficultyRecipe,
                instruction = dto.instruction,
                portion = dto.portion,
                serveFood = dto.serveFood,
                subTitle = dto.subTitle,
                timePrepared = dto.timePrepared,
                title = dto.title,
                status = RecipeStatus.published,
                userId = userId,
                createdAt = Timestamp.from(Date().toInstant()),
                updatedAt = Timestamp.from(Date().toInstant()),
                totalViews = 0
            )
        )
    }

    fun update(dto: RecipeUpdateDto, id: String): RecipeEntity {
        val recipe = recipeRepository.findById(id).getOrNull()
            ?: throw BusinessException("Receita não encontrada")
        if (recipe.status == RecipeStatus.blocked) {
            throw BusinessException("Está receita não pode ser alterada.")
        }
        return recipeRepository.save(
            recipe.copy(
                details = dto.details,
                difficultyRecipe = dto.difficultyRecipe,
                instruction = dto.instruction,
                portion = dto.portion,
                serveFood = dto.serveFood,
                subTitle = dto.subTitle,
                timePrepared = dto.timePrepared,
                title = dto.title,
                status = dto.status,
                updatedAt = Timestamp.from(Date().toInstant())
            )
        )
    }

    fun createImage(id: String, file: MultipartFile, isThumb: Boolean = false) {
        val recipe = recipeRepository.findById(id).getOrNull()
            ?: throw BusinessException("Receita não encontrada")

        if (isThumb) {
            val quantity = recipeImageRepository.countByRecipeIdAndThumb(recipe.id!!, true)
            if (quantity > 1) throw BusinessException("Receita só pode ter uma capa")
        } else {
            val quantity = recipeImageRepository.countByRecipeId(recipe.id!!)
            if (quantity > 10) throw BusinessException("Receita só pode ter 10 imagens")
        }

        val image = recipeImageRepository.save(
            RecipeImageEntity(
                recipeId = recipe.id
            )
        )
        recipeBucketRepository.saveImage(image.id!!, file)
        val linkImage = recipeBucketRepository.getLinkImage(image.id)
        recipeImageRepository.save(
            image.copy(
                size = file.size,
                name = file.name,
                link = linkImage,
                type = file.contentType ?: "n/a",
                thumb = isThumb
            )
        )
    }

    fun deleteImage(id: String) {
        recipeImageRepository.findById(id).getOrNull()
            ?: throw BusinessException("Imagem não encontrada")
        recipeBucketRepository.deleteImage(id)
        recipeImageRepository.deleteById(id)
    }

    fun createRecipeIngredient(dto: RecipeIngredientCreateDto, recipeId: String): RecipeIngredientEntity {
        recipeRepository.findById(recipeId).getOrNull()
            ?: throw BusinessException("Receita não encontrada")

        ingredientRepository.findById(dto.ingredientId).getOrNull()
            ?: throw BusinessException("Ingrediente não encontrada")

        return recipeIngredientRepository.save(
            RecipeIngredientEntity(
                recipeId = recipeId,
                ingredientId = dto.ingredientId,
                quantity = dto.quantity,
                unit = dto.unit,
            )
        )
    }

    fun deleteIngredient(id: String) {
        ingredientRepository.findById(id).getOrNull()
            ?: throw BusinessException("Ingrediente não encontrada")
        ingredientRepository.deleteById(id)
    }

    fun findIngredients(recipeId: String): List<RecipeIngredientListDto> {
        val result = recipeIngredientRepository.findByRecipeId(recipeId)
        return result.map {
            val ingredient = ingredientRepository.findById(it.ingredientId)
            RecipeIngredientListDto(
                id = it.id,
                recipeId = it.recipeId,
                ingredientId = it.ingredientId,
                unit = it.unit,
                quantity = it.quantity,
                ingredientName = ingredient.get().name
            )
        }
    }
}