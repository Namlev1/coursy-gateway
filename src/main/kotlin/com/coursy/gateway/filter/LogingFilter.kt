package com.coursy.gateway.filter

import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class LoggingFilter : GlobalFilter, Ordered {
    private val logger = LoggerFactory.getLogger(LoggingFilter::class.java)

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val request = exchange.request
        val requestPath = request.uri.path
        val method = request.method

        
        logger.info("Incoming request: {} {} -> Target: {}",
            method,
            requestPath,
            exchange.getAttribute<Any>("org.springframework.cloud.gateway.support.ServerWebExchangeUtils.gatewayRequestUrl")
        )

        return chain.filter(exchange).then(
            Mono.fromRunnable {
                val response = exchange.response
                val statusCode = response.statusCode

                
                logger.info("Response: {} {} - Status: {}",
                    method,
                    requestPath,
                    statusCode
                )
            }
        )
    }

    override fun getOrder(): Int = Ordered.LOWEST_PRECEDENCE
}