package com.coursy.gateway.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GatewayConfig {
    @Value("\${services.auth.url:http://auth-service:8080}")
    private lateinit var authServiceUrl: String

    @Value("\${services.users.url:http://users-service:8080}")
    private lateinit var usersServiceUrl: String

    @Value("\${services.platforms.url:http://platforms-service:8080}")
    private lateinit var platformsServiceUrl: String

    @Value("\${services.courses.url:http://courses-service:8080}")
    private lateinit var coursesServiceUrl: String

    @Value("\${services.content.url:http://content-service:8080}")
    private lateinit var contentServiceUrl: String

    @Bean
    fun routeLocator(builder: RouteLocatorBuilder): RouteLocator {
        
        return builder.routes()
            .route("auth-service") { r ->
                r.path("/api/auth/**")
                    .uri(authServiceUrl)
            }
            .route("users-service") { r ->
                r.path("/api/users/**")
                    .uri(usersServiceUrl)
            }
            .route("platforms-service") { r ->
                r.path("/api/platforms/**")
                    .uri(platformsServiceUrl)
            }
            .route("courses-service") { r ->
                r.path("/api/courses/**")
                    .uri(coursesServiceUrl)
            }
            .route("content-service") { r ->
                r.path("/api/content/**")
                    .uri(contentServiceUrl)
            }
            .build()
    }
}