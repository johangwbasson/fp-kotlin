package net.johanbasson

sealed class ApiError(val message: String)

class UserDoesNotExists : ApiError("User does not exists")
class PasswordsDoesNotMatch : ApiError("Passwords does not match")