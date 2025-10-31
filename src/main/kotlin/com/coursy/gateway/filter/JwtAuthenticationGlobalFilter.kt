package com.coursy.gateway.filter

import arrow.core.Either
import arrow.core.flatMap
import com.coursy.gateway.failure.Failure
import com.coursy.gateway.failure.JwtFailure
import com.coursy.gateway.util.JwtUtil
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationGlobalFilter(
    private val jwtUtil: JwtUtil
) : GlobalFilter, Ordered {

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val request = exchange.request

        val authHeader = request.headers.getFirst("Authorization")
        if (authHeader.isNullOrBlank()) {
            return chain.filter(exchange)
        }

        val validationResult = jwtUtil.parseJwt(request)
            .flatMap { token ->
                if (jwtUtil.isJwtTokenValid(token)) {
                    Either.Right(token)
                } else {
                    Either.Left(JwtFailure.InvalidToken)
                }
            }

        return validationResult.fold(
            { failure -> handleUnauthorized(exchange, failure) },
            { _ -> chain.filter(exchange) }
        )
    }

    private fun handleUnauthorized(exchange: ServerWebExchange, failure: Failure): Mono<Void> {
        val response = exchange.response
        response.statusCode = HttpStatus.UNAUTHORIZED
        response.headers.add("Content-Type", "application/json")
        response.headers.add("WWW-Authenticate", "Bearer") // ← DODANE (RFC standard)

        val body = """{"error": "${failure.message()}"}"""
        val buffer = response.bufferFactory().wrap(body.toByteArray())
        return response.writeWith(Mono.just(buffer))
    }

    override fun getOrder(): Int = -1
}