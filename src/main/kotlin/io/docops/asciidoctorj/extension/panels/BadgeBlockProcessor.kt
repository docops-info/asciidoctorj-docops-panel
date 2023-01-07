package io.docops.asciidoctorj.extension.panels

import org.asciidoctor.ast.ContentModel
import org.asciidoctor.ast.StructuralNode
import org.asciidoctor.extension.BlockProcessor
import org.asciidoctor.extension.Contexts
import org.asciidoctor.extension.Name
import org.asciidoctor.extension.Reader

@Name("badge")
@Contexts(Contexts.LISTING)
@ContentModel(ContentModel.COMPOUND)
class BadgeBlockProcessor : BlockProcessor() {

    private var server = "http://localhost:8010/extension"
    //image:http://localhost:8010/extension/api/badge/item?label=ABC&message=512&color=RED&fname=abc.svg[]
    override fun process(parent: StructuralNode, reader: Reader, attributes: MutableMap<String, Any>): Any? {
        var content = subContent(reader, parent)
        val remoteServer = parent.document.attributes["panel-server"]
        if (remoteServer != null) {
            remoteServer as String
            server = remoteServer
        }
        val backend = parent.document.getAttribute("backend") as String
        val lines = mutableListOf<String>()

        content.lines().forEach {
            line ->
            val payload = compressString(" $line")
            var imageLink = "image:"
            if ("pdf" == backend) {
                imageLink = "image::"
            }
            val split = line.split("|")
            var link = ""
            if(split.size>2 ) {
                link = ",link=\"${split[2]}\""
            }
            val str = "$imageLink$server/api/badge/item?payload=$payload&finalname=abc.svg[format=svg $link] "
            lines.add(str)
        }
        parseContent(parent, lines)
        return null
    }


}