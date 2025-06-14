package com.flavor.recipes.profile.controllers

import com.flavor.recipes.core.BusinessException
import com.flavor.recipes.profile.dtos.ProfileNameDto
import com.flavor.recipes.profile.dtos.ValidateName
import com.flavor.recipes.profile.entities.ProfileEntity
import com.flavor.recipes.profile.entities.UpdateProfileDto
import com.flavor.recipes.profile.repositories.ProfileBucketRepository
import com.flavor.recipes.profile.services.ProfileService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.sql.Timestamp
import java.util.*


@RestController
@RequestMapping("/profile")
@Tag(name = "Profile")
class ProfileController {
    private val usernameRegex = Regex(
        "^(?![_.])[a-zA-Z0-9._ ]{1,30}(?<![_.])$"
    );

    @Autowired
    lateinit var profileService: ProfileService

    @Autowired
    lateinit var bucketRepository: ProfileBucketRepository


    @GetMapping("/{userId}")
    @ResponseBody
    fun getProfile(@PathVariable userId: String): ProfileEntity {
        return profileService.byId(userId)
    }

    @PostMapping("/{userId}")
    fun updateProfile(
        @RequestBody body: UpdateProfileDto,
        @PathVariable userId: String,
    ): ProfileEntity {
        if ((body.name?.isBlank() == true)) throw BusinessException("Nome não pode ser vazio")
        val find = profileService.byId(userId, body.name)
        return profileService.save(
            find.copy(
                biography = body.biography,
                name = body.name,
            )
        )
    }

    @PutMapping("/{userId}/image")
    fun handleFileUpload(
        @RequestPart file: MultipartFile?,
        @PathVariable userId: String,
    ): ProfileEntity {
        var image: String? = null
        val find = profileService.byId(userId)
        if (file != null) {
            bucketRepository.saveImage(userId, file)
            image = bucketRepository.getLinkImage(userId)
        }
        return profileService.save(find.copy(image = image, updatedAt = Timestamp.from(Date().toInstant())))
    }


    @PutMapping("/{userId}/name")
    fun updateName(
        @RequestBody profileName: ProfileNameDto,
        @PathVariable userId: String,
    ): ProfileEntity {
        val resultValidateName = validateName(userId, profileName.name)
        if (!resultValidateName.isValid) {
            throw BusinessException(resultValidateName.message)
        }
        val userFind = profileService.byId(userId, profileName.name)
        return profileService.save(
            userFind.copy(
                name = profileName.name,
                updatedAt = Timestamp.from(Date().toInstant())
            )
        )
    }

    @GetMapping("/{userId}/name/{name}/validate")
    fun validateName(@PathVariable userId: String, @PathVariable name: String): ValidateName {
        if (!usernameRegex.matches(name)) {
            return ValidateName(
                name = name,
                isValid = false,
                message = "Nome no formato inválido"
            )
        }
        val find = profileService.findByName(name)
        if (find != null) {
            if (find.userId == userId) {
                return ValidateName(
                    name = name,
                    isValid = false,
                    message = "Você já usa esse nome"
                )
            }
            return ValidateName(
                name = name,
                isValid = false,
                message = "Esse nome ja está em uso, tente outro"
            )
        }
        return ValidateName(
            name = name,
            isValid = true,
            message = "Nome valido para uso"
        )
    }

}