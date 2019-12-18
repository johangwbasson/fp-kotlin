package net.johanbasson

import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.http4k.server.ApacheServer
import org.http4k.server.asServer

fun main(args: Array<String>) {
    val secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
    Server(secretKey).asServer(ApacheServer(7125)).start()
}