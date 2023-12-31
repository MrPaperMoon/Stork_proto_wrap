package io.stork.client.util

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.beBlank
import io.kotest.matchers.types.instanceOf
import io.stork.client.ApiClient
import io.stork.client.ApiClientConfig
import io.stork.client.ApiMediaType
import io.stork.client.ApiResult
import io.stork.client.BasicSessionProvider
import io.stork.client.CloseReason
import io.stork.client.StorkServers
import io.stork.client.exceptions.ConnectionClosedException
import io.stork.proto.client.auth.LoginRequest
import io.stork.proto.client.session.GenerateSessionRequest
import io.stork.proto.client.session.GenerateSessionResponse
import io.stork.proto.client.websocket.Echo
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

@OptIn(ExperimentalTime::class)
class SmokeTest {
    private val generateSessionRequest = GenerateSessionRequest(
        installation_id = "kotlin-api-client-test"
    )
    private val selectedServer = StorkServers.staging
    private lateinit var sessionProvider: BasicSessionProvider

    private fun createApiClient(mediaType: ApiMediaType): ApiClient {
        sessionProvider = BasicSessionProvider()
        return ApiClient(ApiClientConfig(domainNameProvider = { selectedServer.address }, mediaType = mediaType), sessionProvider = sessionProvider)
    }

    @ParameterizedTest
    @EnumSource(ApiMediaType::class)
    fun itCanDoSuccessfulRestApiCall(mediaType: ApiMediaType) = suspendTest {
        val client = createApiClient(mediaType)
        val response = client.session.generate(generateSessionRequest)
        response shouldBe instanceOf<ApiResult.Success<GenerateSessionResponse>>()
        val result = response.getOrThrow()
        result.jwt_token shouldNot beBlank()
    }

    @ParameterizedTest
    @EnumSource(ApiMediaType::class)
    fun itCanHandleFailedRestApiCall(mediaType: ApiMediaType) = suspendTest {
        val client = createApiClient(mediaType)
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
        val client = createApiClient(mediaType)
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
        val client = createApiClient(mediaType)
        val sessionId = client.generateSessionId()

        val webSocket = client.startWebSocket(sessionId)
        webSocket.isNewSession.first() shouldBe true
        webSocket.close()

        val newWebSocket = client.startWebSocket(sessionId)
        newWebSocket.isNewSession.first() shouldBe false
        newWebSocket.close()
    }

    @ParameterizedTest
    @EnumSource(ApiMediaType::class)
    fun youCantUseClosedWebSocket(mediaType: ApiMediaType) = suspendTest {
        val client = createApiClient(mediaType)
        val sessionId = client.generateSessionId()

        val webSocket = client.startWebSocket(sessionId)
        webSocket.close()

        shouldThrow<ConnectionClosedException> {
            withTimeout(Duration.seconds(1)) {
                webSocket.sendEcho(Echo("FooBar"))
            }
        }
    }

    @ParameterizedTest
    @EnumSource(ApiMediaType::class)
    fun closedWebSocketClosesReceivedFlows(mediaType: ApiMediaType) = suspendTest {
        val client = createApiClient(mediaType)
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
        sessionProvider.sessionId = myJwtToken
        return myJwtToken
    }
}
