package net.johanbasson

import arrow.core.*
import org.mindrot.jbcrypt.BCrypt
import java.util.*

data class User(val id: UUID, val email: String, val password: String)

fun getUserByEmail(email: String): Either<ApiError, Option<User>> {
    return if (email == "admin") {
        Right(Some(User(UUID.randomUUID(), "admin", BCrypt.hashpw("admin", BCrypt.gensalt()))))
    } else {
        Right(None)
    }
}