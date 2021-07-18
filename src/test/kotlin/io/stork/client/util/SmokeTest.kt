package io.stork.client.util

import io.kotest.matchers.shouldBe
import io.stork.client.ApiResult
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.instanceOf
import io.stork.client.ApiClient
import io.stork.client.ApiClientConfig
import io.stork.client.ApiMediaType
import io.stork.proto.auth.LoginRequest
import io.stork.proto.session.GenerateSessionRequest
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class SmokeTest {
    @ParameterizedTest
    @EnumSource(ApiMediaType::class)
    fun itWorks(mediaType: ApiMediaType) = suspendTest {
        val client = ApiClient(ApiClientConfig("dev.stork.io", mediaType = mediaType))
        val response = client.session.generate(
                GenerateSessionRequest(
                        installation_id = "kotlin-api-client-test"
                )
        ).getOrThrow()
        val myJwtToken = response.jwt_token
        myJwtToken shouldNotBe null
        client.sessionJwtToken = myJwtToken

        val loginResult = client.auth.login(
                LoginRequest(
                        email = "foo_bar@foo_bar.com",
                        password = "kokoko"
                )
        )
        loginResult shouldBe instanceOf<ApiResult.AuthenticationError>()
    }
}