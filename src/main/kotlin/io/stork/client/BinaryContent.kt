package io.stork.client

import io.ktor.http.*
import java.io.File
import java.io.InputStream

typealias UploadStatusCallback = suspend (bytesSent: Long, totalBytes: Long) -> Unit

interface BinaryContent {
    val contentType: String
        get() = "application/octet-stream"
    val name: String
        get() = "data"

    val size: Long?
        get() = null

    fun open(): InputStream
}

class FileBinaryContent(val file: File) : BinaryContent {
    override val contentType: String
        get() = ContentType.defaultForFile(file).toString()
    override val name: String
        get() = file.name

    override val size: Long
        get() = file.length()

    override fun open(): InputStream = file.inputStream()
}