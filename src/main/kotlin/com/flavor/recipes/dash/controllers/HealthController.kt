package com.flavor.recipes.dash.controllers

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/dash")
@Tag(name = "Dash")
class HealthController {
    @GetMapping("/health")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun get() {
        return
    }
}