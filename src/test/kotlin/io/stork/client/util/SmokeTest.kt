package io.stork.client.util

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.beBlank
import io.kotest.matchers.types.instanceOf
import io.stork.client.*
import io.stork.client.exceptions.ConnectionClosedException
import io.stork.proto.auth.LoginRequest
import io.stork.proto.session.GenerateSessionRequest
import io.stork.proto.session.GenerateSessionResponse
import io.stork.proto.websocket.Echo
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class SmokeTest {
    private val generateSessionRequest = GenerateSessionRequest(
            installation_id = "kotlin-api-client-test"
    )
    private val selectedServer = Servers.staging

    @ParameterizedTest
    @EnumSource(ApiMediaType::class)
    fun itCanDoSuccessfulRestApiCall(mediaType: ApiMediaType) = suspendTest {
        val client = ApiClient(ApiClientConfig(selectedServer, mediaType = mediaType))
        val response = client.session.generate(generateSessionRequest)
        response shouldBe instanceOf<ApiResult.Success<GenerateSessionResponse>>()
        val result = response.getOrThrow()
        result.jwt_token shouldNot beBlank()
    }

    @ParameterizedTest
    @EnumSource(ApiMediaType::class)
    fun itCanHandleFailedRestApiCall(mediaType: ApiMediaType) = suspendTest {
        val client = ApiClient(ApiClientConfig(selectedServer, mediaType = mediaType))
        val loginResult = client.auth.login(
                LoginRequest(
                        email = "foo_bar@foo_bar.com",
                        password = "kokoko"
                )
        )
        loginResult shouldBe instanceOf<ApiResult.AuthenticationError>()
    }

    @ParameterizedTest
    @EnumSource(ApiMediaType::class)
    fun itCanDoBasicUsageOfWebSocket(mediaType: ApiMediaType) = suspendTest {
        val client = ApiClient(ApiClientConfig(selectedServer, mediaType = mediaType))
        val sessionId = client.generateSessionId()

        val echoMessage = "Hello, ☃"
        val webSocket = client.startWebSocket(sessionId)

        webSocket.sendEcho(Echo(echoMessage))
        val echoResponse = webSocket.receiveEcho.first()
        echoResponse.message shouldBe echoMessage

        webSocket.close()
        webSocket.closeReason.value shouldBe CloseReason.GracefulClose
    }

    @ParameterizedTest
    @EnumSource(ApiMediaType::class)
    fun itCanDeliverSessionInfoOfWebSocket(mediaType: ApiMediaType) = suspendTest {
        val client = ApiClient(ApiClientConfig(selectedServer, mediaType = mediaType))
        val sessionId = client.generateSessionId()

        val webSocket = client.startWebSocket(sessionId)
        webSocket.isNewSession.first() shouldBe true
        webSocket.close()

        val newWebSocket = client.startWebSocket(sessionId)
        newWebSocket.isNewSession.first() shouldBe false
        newWebSocket.close()
    }

    @OptIn(ExperimentalTime::class)
    @ParameterizedTest
    @EnumSource(ApiMediaType::class)
    fun youCantUseClosedWebSocket(mediaType: ApiMediaType) = suspendTest {
        val client = ApiClient(ApiClientConfig(selectedServer, mediaType = mediaType))
        val sessionId = client.generateSessionId()

        val webSocket = client.startWebSocket(sessionId)
        webSocket.close()

        shouldThrow<ConnectionClosedException> {
            withTimeout(Duration.seconds(1)) {
                webSocket.sendEcho(Echo("FooBar"))
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    @ParameterizedTest
    @EnumSource(ApiMediaType::class)
    fun closedWebSocketClosesReceivedFlows(mediaType: ApiMediaType) = suspendTest {
        val client = ApiClient(ApiClientConfig(selectedServer, mediaType = mediaType))
        val sessionId = client.generateSessionId()

        val webSocket = client.startWebSocket(sessionId)
        val allReceivedEcho = async {
            webSocket.receiveEcho.toList()
        }
        val echo = Echo("No, I am server!")
        webSocket.sendEcho(echo)
        webSocket.receiveEcho.first() shouldBe echo
        webSocket.close()

        withTimeout(Duration.seconds(1)) {
             allReceivedEcho.await() shouldBe listOf(echo)
        }
    }



    private suspend fun ApiClient.generateSessionId(): String {
        val response = session.generate(generateSessionRequest).getOrThrow()
        val myJwtToken = response.jwt_token
        myJwtToken shouldNotBe null
        sessionJwtToken = myJwtToken
        return myJwtToken
    }
}