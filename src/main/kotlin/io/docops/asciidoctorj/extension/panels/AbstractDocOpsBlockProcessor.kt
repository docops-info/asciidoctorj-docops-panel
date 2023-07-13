package io.docops.asciidoctorj.extension.panels

import org.asciidoctor.ast.Block
import org.asciidoctor.ast.StructuralNode
import org.asciidoctor.extension.BlockProcessor
import org.asciidoctor.extension.Reader
import org.asciidoctor.log.LogRecord
import org.asciidoctor.log.Severity

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
        if (serverPresent(server, parent, this, localDebug)) {
            var type ="SVG"
            if("pdf" == backend) {
                type = "PDF"
            }
            val payload: String = try {
                compressString(content)
            } catch (e: Exception) {
                log(LogRecord(Severity.ERROR, parent.sourceLocation, e.message))
                ""
            }
            val lines = mutableListOf<String>()
            val url = buildUrl(
                payload = payload,
                scale = scale,
                title = title,
                type = type,
                role = role,
                block = parent
            )
            if(localDebug) {
                println(url)
            }
            lines.add(url)
            parseContent(block, lines)
        }
        return block
    }
    abstract fun buildUrl(
        payload: String,
        scale: String,
        title: String,
        type: String,
        role: String,
        block: StructuralNode
    ): String
}