package net.johanbasson

import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import jdk.net.SocketFlow
import org.http4k.core.*
import org.http4k.core.Status.Companion.OK
import org.http4k.filter.CorsPolicy.Companion.UnsafeGlobalPermissive
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.ServerFilters
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import javax.crypto.SecretKey

object Server {
    operator fun invoke(secretKey: SecretKey): RoutingHttpHandler {
        return DebuggingFilters
            .PrintRequestAndResponse()
            .then(ServerFilters.Cors(UnsafeGlobalPermissive))
            .then(ServerFilters.CatchLensFailure)
            .then(
                routes(
                    "/authenticate" bind Method.POST to Authenticate(secretKey),
                    "/ping" bind Method.GET to { Response(OK).body("pong!") }
                )
            )
    }
}