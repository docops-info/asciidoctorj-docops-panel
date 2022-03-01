package io.docops.asciidoctorj.extension.panels

import org.asciidoctor.ast.Document
import org.asciidoctor.extension.DocinfoProcessor
import org.asciidoctor.extension.Location
import org.asciidoctor.extension.LocationType

@Location(LocationType.HEADER)
class TocBotHeaderDocInfoProcessor: DocinfoProcessor() {
    override fun process(document: Document): String {
        return """
            <style>
              /* Tocbot dynamic TOC, works with tocbot 3.0.2 */
              /* Source: https://github.com/asciidoctor/asciidoctor/issues/699#issuecomment-321066006 */
              #tocbot a.toc-link.node-name--H1{ font-style: italic }
              @media screen{
                #tocbot > ul.toc-list{ margin-bottom: 0.5em; margin-left: 0.125em }
                #tocbot ul.sectlevel0, #tocbot a.toc-link.node-name--H1 + ul{
                  padding-left: 0 }
                #tocbot a.toc-link{ height:100% }
                .is-collapsible{ max-height:3000px; overflow:hidden; }
                .is-collapsed{ max-height:0 }
                .is-active-link{ font-weight:700 }
              }
              @media print{
                #tocbot a.toc-link.node-name--H4{ display:none }
              }
              .toc{overflow-y:auto}.toc>ul{overflow:hidden;position:relative}.toc>ul li{list-style:none}.toc-list{margin:0;padding-left:10px}a.toc-link{color:currentColor;height:100%}.is-collapsible{max-height:1000px;overflow:hidden;transition:all 300ms ease-in-out}.is-collapsed{max-height:0}.is-position-fixed{position:fixed !important;top:0}.is-active-link{font-weight:700}.toc-link::before{background-color:#EEE;content:' ';display:inline-block;height:inherit;left:0;margin-top:-1px;position:absolute;width:2px}.is-active-link::before{background-color:#54BC4B}
              /*.transition--300{transition:all 300ms ease-in-out}.toc{height:100%;width:280px;transform:translateX(0)}.content h1:first-child,.content h2:first-child{padding-top:0;margin-top:0}.title{font-size:3em}.content{margin-bottom:95vh}.content ul,.content ol{list-style:inherit}.content a{color:#0977c3;text-decoration:none;border-bottom:1px solid #EEE;transition:all 300ms ease}.content a.no-decoration{border-bottom:0}.content a:hover{border-bottom:1px solid #0977c3}.content a:hover.no-decoration{border-bottom:0}a.toc-link{text-decoration:none}.try-it-container{transform:translateY(84%)}.try-it-container.is-open{transform:translateY(0%)}.page-content{display:block !important}.hljs{display:block;background:white;padding:0.5em;color:#333333;overflow-x:auto}.hljs-comment,.hljs-meta{color:#969896}.hljs-string,.hljs-variable,.hljs-template-variable,.hljs-strong,.hljs-emphasis,.hljs-quote{color:#df5000}.hljs-keyword,.hljs-selector-tag,.hljs-type{color:#a71d5d}.hljs-literal,.hljs-symbol,.hljs-bullet,.hljs-attribute{color:#0086b3}.hljs-section,.hljs-name{color:#63a35c}.hljs-tag{color:#333333}.hljs-title,.hljs-attr,.hljs-selector-id,.hljs-selector-class,.hljs-selector-attr,.hljs-selector-pseudo{color:#795da3}.hljs-addition{color:#55a532;background-color:#eaffea}.hljs-deletion{color:#bd2c00;background-color:#ffecec}.hljs-link{text-decoration:underline}.toc-icon{position:fixed;top:0;right:0}#toc:checked ~ .toc{box-shadow:0 0 5px #c8c8c8;transform:translateX(0)}.toc{background-color:rgba(255,255,255,0.9);transform:translateX(-100%)}.toc.toc-right{transform:translateX(100%);right:0}@media (min-width: 52em){.toc{transform:translateX(0)}.toc.toc-right{transform:translateX(0);right:calc((100% - 48rem - 4rem) / 2)}.toc-icon{display:none}.try-it-container{display:block}.content{margin-left:280px}.toc-right ~ .content{margin-left:0;margin-right:280px}}*{box-sizing:border-box}body{font-size:1.2rem;font-family:-apple-system, BlinkMacSystemFont, "Segoe UI", "Roboto", "Oxygen", "Ubuntu", "Cantarell", "Fira Sans", "Droid Sans", "Helvetica Neue", sans-serif}h1,h2,h3,h4,h5,h6{padding-top:0.5em}p{margin-top:0.25rem}pre{display:block;background:#f7f7f7;border-radius:2px;border:1px solid #e0e0e0;padding:2px;line-height:1.2;margin-bottom:10px;overflow:auto;white-space:pre-wrap}code{display:inline;font-size:.8em;max-width:100%}
                */
            </style>
        """.trimIndent()
    }
}