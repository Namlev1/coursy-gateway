package com.coursy.gateway.failure

sealed class JwtFailure : Failure {
    data object MissingHeader : JwtFailure()
    data object InvalidHeader : JwtFailure()
    data object InvalidToken : JwtFailure()

    override fun message(): String = when (this) {
        MissingHeader -> "Missing Authorization header"
        InvalidHeader -> "Invalid Authorization header"
        InvalidToken -> "Invalid JWT token"
    }
}