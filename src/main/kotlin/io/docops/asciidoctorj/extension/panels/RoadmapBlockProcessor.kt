package io.docops.asciidoctorj.extension.panels

import org.asciidoctor.ast.ContentModel
import org.asciidoctor.ast.StructuralNode
import org.asciidoctor.extension.Contexts
import org.asciidoctor.extension.Name

@Name("roadmap")
@Contexts(Contexts.LISTING)
@ContentModel(ContentModel.COMPOUND)
class RoadmapBlockProcessor : AbstractDocOpsBlockProcessor(){


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
        val fname = System.currentTimeMillis()
        val numChars = getCharLength(attributes, 32)
        return """
image::$webserver/api/roadmap/?payload=$payload&type=SVG&scale=$scale&numChars=$numChars&title=$title&useDark=$useDark&filename=ghi$fname.svg[$opts]
            
link:$webserver/api/roadmap/?payload=$payload&type=XLS&scale=$scale&numChars=$numChars&title=$title&useDark=$useDark&filename=jkl.xlsx[Excel]
            """
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
        val fname = System.currentTimeMillis()
        val numChars = getCharLength(attributes, 32)
        return "$webserver/api/roadmap/?payload=$payload&type=SVG&scale=$scale&numChars=$numChars&title=$title&useDark=$useDark&filename=ghi$fname.svg"

    }
}