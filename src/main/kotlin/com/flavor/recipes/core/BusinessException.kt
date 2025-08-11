package com.flavor.recipes.core

class BusinessException(override val message: String, val codeError: String? = null) : Exception()

