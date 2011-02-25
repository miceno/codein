
import java.net.URLEncoder
import org.apache.commons.lang.StringEscapeUtils


String internationalString  = "áéíóúñ"
String escapeHtml           = "España Sánchez Jörgen"

StringWriter writer= new StringWriter()
def builder = new groovy.xml.MarkupBuilder( writer)
   
def html=   builder.html{
     bug { mkp.yieldUnescaped( StringEscapeUtils.escapeXml(internationalString)) }
     bug { mkp.yield( "€") }
     bugyield { mkp.yieldUnescaped( StringEscapeUtils.escapeXml (escapeHtml)) }
     fullworkaround { mkp.yieldUnescaped( StringEscapeUtils.escapeXml( internationalString)) }
   }
   
String result= writer.toString()
   println result
   /*
   assert result.contains( internationalString)
   assert result.contains( escapeHtml)
   assert result.contains( StringEscapeUtils.escapeXml( internationalString))
*/