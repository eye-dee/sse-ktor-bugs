package com.eyedee.ssektorbugs.ktor

import com.eyedee.ssektorbugs.controller.SseController
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.sse.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.time.Duration.Companion.minutes

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
)
@ActiveProfiles("test")
class KtorClientTest {

    @Test
    fun `receive sse with GET`() = runTest(timeout = 10.minutes) {

        val client = HttpClient {
            install(SSE) {
                showCommentEvents()
                showRetryEvents()
            }
        }
        client.sse(
            host = "localhost",
            port = 8080,
            path = "sse/get",
        ) {
            incoming.collect { event ->
                println("Event from server:")
                println(event)
            }
        }
        client.close()
    }

    @Test
    fun `receive sse with POST`() = runTest(timeout = 10.minutes) {

        val client = HttpClient {
            install(SSE) {
                showCommentEvents()
                showRetryEvents()
            }
        }
        client.sse(
            host = "localhost",
            port = 8080,
            path = "sse/post",
            request = {
                method = HttpMethod.Post
            }
        ) {
            incoming.collect { event ->
                println("Event from server:")
                println(event)
            }
        }
        client.close()
    }

    @Test
    fun `receive sse with POST with body`() = runTest(timeout = 10.minutes) {

        val client = HttpClient {
            install(SSE) {
                showCommentEvents()
                showRetryEvents()
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }
        client.sse(
            host = "localhost",
            port = 8080,
            path = "sse/body-post",
            request = {
                method = HttpMethod.Post
                contentType(ContentType.Application.Json)
                setBody("""{"data": "data"}""")
            }
        ) {
            incoming.collect { event ->
                println("Event from server:")
                println(event)
            }
        }
        client.close()
    }

    @Test
    fun `receive sse with POST with body and content negotiation`() =
        runTest(timeout = 10.minutes) {

            val client = HttpClient {
                install(SSE) {
                    showCommentEvents()
                    showRetryEvents()
                }
                install(ContentNegotiation) {
                    jackson()
                }
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.ALL
                }
            }
            client.sse(
                host = "localhost",
                port = 8080,
                path = "sse/body-post",
                request = {
                    method = HttpMethod.Post
                    contentType(ContentType.Application.Json)
                    accept(ContentType.Text.EventStream)
                    setBody(SseController.RequestDto("some data"))
                }
            ) {
                incoming.collect { event ->
                    println("Event from server:")
                    println(event)
                }
            }
            client.close()
        }

    @Test
    fun `receive sse with GET when logging plugin enabled`() = runTest(timeout = 10.minutes) {

        val client = HttpClient {
            install(SSE) {
                showCommentEvents()
                showRetryEvents()
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }
        client.sse(
            host = "localhost",
            port = 8080,
            path = "sse/get",
        ) {
            incoming.collect { event ->
                println("Event from server:")
                println(event)
            }
        }
        client.close()
    }

}
