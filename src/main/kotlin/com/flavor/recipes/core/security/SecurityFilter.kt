package com.flavor.recipes.core.security

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException


@Component
@Service
class SecurityFilter : OncePerRequestFilter() {
    @Autowired
    lateinit var tokenService: SecurityService

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = recoverToken(request)
        val userId = tokenService.validateToken(token)
        if (userId != null) {
            val authentication = UsernamePasswordAuthenticationToken(userId, "", listOf())
            val context = SecurityContextHolder.getContext()
            context.authentication = authentication
            SecurityContextHolder.setContext(context)
        }
        filterChain.doFilter(request, response)
    }

    private fun recoverToken(request: HttpServletRequest): String? {
        val authHeader = request.getHeader("Authorization") ?: return null
        return authHeader.replace("Bearer ", "")
    }
}