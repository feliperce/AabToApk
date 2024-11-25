package io.github.feliperce.aabtoapk.utils

import io.github.feliperce.aabtoapk.data.remote.response.ErrorResponseType
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

suspend fun RoutingCall.sendErrorResponse(
    httpStatusCode: HttpStatusCode = HttpStatusCode.BadRequest,
    errorResponseType: ErrorResponseType,
    customErrorMsg: String? = null
) {
    this.response.status(httpStatusCode)
    this.respond(errorResponseType.toErrorResponse(customErrorMsg))
}