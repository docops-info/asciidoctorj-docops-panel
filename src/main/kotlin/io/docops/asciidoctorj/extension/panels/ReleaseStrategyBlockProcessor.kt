package io.docops.asciidoctorj.extension.panels

import org.asciidoctor.ast.ContentModel
import org.asciidoctor.ast.StructuralNode
import org.asciidoctor.extension.Contexts
import org.asciidoctor.extension.Name

@Name("release")
@Contexts(Contexts.LISTING)
@ContentModel(ContentModel.COMPOUND)
class ReleaseStrategyBlockProcessor : AbstractDocOpsBlockProcessor(){



    override fun buildUrl(
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
    ): String {
        val animate = attributes.getOrDefault("animate", "ON")
        val numChars = getCharLength(attributes, 32)
        return """
image::$webserver/api/release/?payload=$payload&type=$type&animate=$animate&numchars=$numChars&useDark=$useDark&filename=def.svg[$opts]

link:$webserver/api/release/?payload=$payload&type=XLS&filename=def.xls[Excel]
""".trimIndent()
    }

    override fun getUrl(
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
    ): String {
        val animate = attributes.getOrDefault("animate", "ON")
        val numChars = getCharLength(attributes, 32)
        return "$webserver/api/release/?payload=$payload&type=SVG&animate=$animate&numchars=$numChars&useDark=$useDark&filename=def.svg"

    }
}