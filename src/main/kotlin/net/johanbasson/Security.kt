package net.johanbasson

import arrow.core.*
import arrow.core.extensions.fx
import io.jsonwebtoken.Jwts
import jdk.net.SocketFlow
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import javax.crypto.SecretKey
import net.johanbasson.ApplicationJackson.auto
import org.http4k.core.Response
import org.http4k.core.Status
import org.mindrot.jbcrypt.BCrypt

data class Token(val token: String)

data class AuthenticateRequest(val email: String, val password: String)

object Authenticate {
    private val authenticateRequestLens = Body.auto<AuthenticateRequest>().toLens()
    private val tokenLens = Body.auto<Token>().toLens()
    private val errorLens = Body.auto<ApiError>().toLens()

    operator fun invoke(secretKey: SecretKey): HttpHandler = {
        val authReq = authenticateRequestLens(it)
        when (val result = authenticate(authReq.email, authReq.password, secretKey)) {
            is Either.Right -> tokenLens.inject(result.b, Response(Status.OK))
            is Either.Left -> errorLens.inject(result.a, Response(Status.BAD_REQUEST))
        }
    }
}

fun authenticate(email: String, password: String, secretKey: SecretKey): Either<ApiError, Token> = Either.fx {
    val (opt) = getUserByEmail(email)
    val (user) = checkUserExists(opt)
    val (checked) = checkPassword(user, password)
    val (token) = generateJwtToken(user, secretKey)
    token
}

private fun checkUserExists(opt: Option<User>): Either<ApiError, User> = when (opt) {
    is None -> Left(UserDoesNotExists())
    is Some -> Right(opt.t)
}

private fun checkPassword(user: User, plain: String): Either<ApiError, User> {
    return when (val matches = BCrypt.checkpw(plain, user.password)) {
        true -> Right(user)
        else -> Left(PasswordsDoesNotMatch())
    }
}

private fun generateJwtToken(user: User, secretKey: SecretKey): Either<ApiError, Token> {
    val token = Jwts.builder()
        .setSubject(user.id.toString())
        .signWith(secretKey)
        .compact()

    return Right(Token(token))
}



