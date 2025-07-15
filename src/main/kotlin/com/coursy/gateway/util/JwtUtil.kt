package com.coursy.gateway.util

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.coursy.gateway.failure.JwtFailure
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component

@Component
class JwtUtil(
    @Value("\${jwt.secret}")
    private var jwtSecret: String,
) {
    fun isJwtTokenValid(authToken: String): Boolean =
        runCatching {
            JWT.require(Algorithm.HMAC256(jwtSecret)).build().verify(authToken)
            true
        }.getOrElse { false }

    fun parseJwt(request: ServerHttpRequest): Either<JwtFailure, String> {
        val authHeader = request.headers.getFirst("Authorization")
            ?: return JwtFailure.MissingHeader.left()
        
        if (!authHeader.startsWith("Bearer ")) {
            return JwtFailure.InvalidHeader.left()
        }

        return authHeader
            .removePrefix("Bearer ")
            .trim()
            .right()
    }
}