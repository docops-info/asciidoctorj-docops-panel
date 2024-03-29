package io.docops.asciidoctorj.extension.panels

import org.asciidoctor.ast.Block
import org.asciidoctor.ast.StructuralNode
import org.asciidoctor.extension.BlockProcessor
import org.asciidoctor.extension.Reader
import org.asciidoctor.log.LogRecord
import org.asciidoctor.log.Severity
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.time.Duration

abstract class AbstractDocOpsBlockProcessor: BlockProcessor() {
    protected var server = "http://localhost:8010/extension"
    protected var webserver = "http://localhost:8010/extension"
    protected var localDebug = false
    protected fun imageBlock(
        env: String,
        pdf: String,
        parent: StructuralNode,
        imageUrl: String,
        fig: String
    ): Block {
        val block =
            when {
                "idea" == env && "pdf" != pdf -> {
                    createBlock(parent, "pass", "<img src='$imageUrl'></img>")
                }
                else -> {
                    val blockAttrs = mutableMapOf<String, Any>(
                        "role" to "docops.io.panels",
                        "target" to imageUrl,
                        "alt" to "IMG not available",
                        "title" to "Figure. $fig",
                        "interactive-option" to "",
                        "format" to "svg"
                    )
                    createBlock(parent, "image", ArrayList(), blockAttrs, HashMap())
                }
            }
        return block
    }
    override fun process(parent: StructuralNode, reader: Reader, attributes: MutableMap<String, Any>): Any? {
        val debug = parent.document.attributes["local-debug"]
        if (debug != null) {
            debug as String
            localDebug = debug.toBoolean()
        }
        val content = subContent(reader, parent, localDebug)
        val remoteServer = parent.document.attributes["panel-server"]
        remoteServer?.let {
            server = remoteServer as String
        }
        val remoteWebserver = parent.document.attributes["panel-webserver"]
        remoteWebserver?.let {
            webserver = it as String
        }
        val scale = attributes.getOrDefault("scale", "1.0") as String
        val block: Block = createBlock(parent, "open", null as String?)
        val title = attributes.getOrDefault("title", "Roadmap Title Here") as String
        val backend = parent.document.getAttribute("backend") as String
        val role = attributes.getOrDefault("role", "center") as String
        val dark = attributes.getOrDefault("useDark", "false") as String
        val idea = parent.document.getAttribute("env", "") as String
        val outlineColor = attributes.getOrDefault("outlineColor", "#7149c6") as String
        val ideaOn = "idea".equals(idea, true)
        val useDark : Boolean = "true".equals(dark, true)
        if (serverPresent(server, parent, this, localDebug)) {
            var type ="SVG"

            val payload: String = try {
                compressString(content)
            } catch (e: Exception) {
                log(LogRecord(Severity.ERROR, parent.sourceLocation, e.message))
                ""
            }
            var opts = "format=svg,opts=inline,align='$role'"
            if(ideaOn) {
                opts = ""
            }
            if("pdf" == backend) {
                type = "PDF"
            }
            val lines = mutableListOf<String>()
            if(ideaOn) {
                val url = getUrl(payload = payload,
                    scale = scale,
                    title = title,
                    type = type,
                    role = role,
                    block = parent,
                    opts = opts,
                    useDark = useDark,
                    attributes = attributes,
                    outlineColor = outlineColor)
                val image = getContentFromServer(url, parent, this, debug = localDebug)
                return createImageBlockFromString(parent, image, role, "970")
            } else {
                val url = buildUrl(
                    payload = payload,
                    scale = scale,
                    title = title,
                    type = type,
                    role = role,
                    block = parent,
                    opts = opts,
                    useDark = useDark,
                    attributes = attributes,
                    outlineColor = outlineColor
                )
                if (localDebug) {
                    println(url)
                }
                lines.addAll(url.lines())
                parseContent(block, lines)
            }
        }
        return block
    }

    protected fun String.encodeUrl(): String {
        return URLEncoder.encode(this, StandardCharsets.UTF_8.toString())
    }

    protected fun getCharLength(attributes: MutableMap<String, Any>, defaultLength: Int) : String {
        return attributes.getOrDefault("numChars", "$defaultLength") as String
    }
    abstract fun buildUrl(
        payload: String,
        scale: String,
        title: String,
        type: String,
        role: String,
        block: StructuralNode,
        opts: String,
        attributes: MutableMap<String, Any>,
        useDark: Boolean,
        outlineColor: String
    ): String

    abstract fun getUrl(
        payload: String,
        scale: String,
        title: String,
        type: String,
        role: String,
        block: StructuralNode,
        opts: String,
        attributes: MutableMap<String, Any>,
        useDark: Boolean,
        outlineColor: String
    ): String

     private fun createImageBlockFromString(parent: StructuralNode, svg: String, role: String, width: String): Block {

        val align = mutableMapOf(
            "right" to "margin-left: auto; margin-right: 0;",
            "left" to "",
            "center" to "margin: auto;"
        )
        val center = align[role.lowercase()]
        val content: String = """
            <div class="openblock">
            <div class="content" style="width: $width;padding: 10px;$center">
            $svg
            </div>
            </div>
        """.trimIndent()
        return createBlock(parent, "pass", content)
    }
}
fun getContentFromServer(url: String, parent: StructuralNode, pb: BlockProcessor, debug: Boolean = false): String {
    if (debug) {
        println("getting image from url $url")
    }
    val client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
        .connectTimeout(Duration.ofSeconds(20))
        .build()
    val request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .timeout(Duration.ofMinutes(1))
        .build()
    return try {
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        response.body()
    } catch (e: Exception) {
        pb.log(LogRecord(Severity.ERROR, parent.sourceLocation, e.message))
        e.printStackTrace()
        ""
    }
}

fun serverPresent(server: String, parent: StructuralNode, pb: BlockProcessor, debug: Boolean = false): Boolean {
    if (debug) {
        println("Checking if server is present ${server}/api/ping")
    }
    val client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
        .connectTimeout(Duration.ofSeconds(20))
        .build()
    val request = HttpRequest.newBuilder()
        .uri(URI.create("$server/api/ping"))
        .timeout(Duration.ofMinutes(1))
        .build()
    return try {
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        (200 == response.statusCode())
    } catch (e: Exception) {
        pb.log(LogRecord(Severity.ERROR, parent.sourceLocation, e.message))
        e.printStackTrace()
        false
    }
}
