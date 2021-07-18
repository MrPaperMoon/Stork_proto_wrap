package io.stork.client.ktor

import com.squareup.wire.Message
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.utils.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.utils.io.*
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

class ProtobufFeature private constructor(
    private val serializer: ProtobufSerializer,
    private val acceptContentTypes: Set<ContentType>) {

    fun canHandle(contentType: ContentType): Boolean = acceptContentTypes.contains(contentType)

    class Config {
        var serializer: ProtobufSerializer? = null
        var acceptContentTypes: Set<ContentType> = setOf(ContentType.parse("application/x-protobuf"))
    }

    companion object Feature : HttpClientFeature<ProtobufFeature.Config, ProtobufFeature> {
        override val key: AttributeKey<ProtobufFeature> = AttributeKey("Protobuf")

        private fun defaultSerializer(): ProtobufSerializer = DefaultProtobufSerializer

        override fun prepare(block: Config.() -> Unit): ProtobufFeature {
            val config = Config().apply(block)
            val serializer = config.serializer ?: defaultSerializer()
            val allowedContentTypes = config.acceptContentTypes.toSet()

            return ProtobufFeature(serializer, allowedContentTypes)
        }

        override fun install(feature: ProtobufFeature, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.Transform) { payload ->
                feature.acceptContentTypes.forEach { context.accept(it) }

                val contentType = context.contentType() ?: return@intercept
                if (!feature.canHandle(contentType)) return@intercept

                context.headers.remove(HttpHeaders.ContentType)

                val serializedContent = when (payload) {
                    Unit -> EmptyContent
                    is EmptyContent -> EmptyContent
                    is Message<*, *> -> feature.serializer.write(payload, contentType)
                    else -> return@intercept
                }

                proceedWith(serializedContent)
            }

            scope.responsePipeline.intercept(HttpResponsePipeline.Transform) { (info, body) ->
                if (body !is ByteReadChannel) return@intercept

                val contentType = context.response.contentType() ?: return@intercept
                if (!feature.canHandle(contentType)) return@intercept

                if (info.type.isSubclassOf(Message::class)) {
                    @Suppress("UNCHECKED_CAST")
                    val messageSubtype = info.type as KClass<out Message<*, *>>
                    val parsedBody = feature.serializer.read(messageSubtype, body.readRemaining())
                    val response = HttpResponseContainer(info, parsedBody)
                    proceedWith(response)
                } else {
                    return@intercept
                }
            }
        }
    }
}