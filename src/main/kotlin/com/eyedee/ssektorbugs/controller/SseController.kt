package com.eyedee.ssektorbugs.controller

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("sse")
class SseController {

    @GetMapping(
        path = ["get"],
        produces = [MediaType.TEXT_EVENT_STREAM_VALUE],
    )
    fun getSseEvents() =
        sseEvents()

    @PostMapping(
        path = ["post"],
        produces = [MediaType.TEXT_EVENT_STREAM_VALUE],
    )
    fun postSseEvents() =
        sseEvents()

    @PostMapping(
        path = ["body-post"],
        produces = [MediaType.TEXT_EVENT_STREAM_VALUE],
    )
    fun postSseEventsWithBody(
        @RequestBody
        request: RequestDto,
    ) =
        sseEvents()

    private fun sseEvents(): Flow<ResponseDto> =
        flow {
            for (i in (1..50)) {
                emit(ResponseDto())
                delay(100)
            }
        }

    data class ResponseDto(
        val data: String = "response"
    )

    data class RequestDto(
        val data: String,
    )
}
