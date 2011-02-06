
import java.net.URLEncoder
import org.apache.commons.lang.StringEscapeUtils


String prueba= "<name>holaÃ¶</name>"

def builder = new groovy.xml.MarkupBuilder()  // construct a builder step(1)

String codec= URLEncoder.encode( prueba, "UTF-8")
String escapeHtml= StringEscapeUtils.escapeXml( "EspaÃ±a SÃ¡nchez JÃ³rgen")

def xml=   builder.html{

     texto prueba
     codificadourlencoder { mkp.yield( codec )}
     codificadohtml mkp.yieldUnescaped( escapeHtml)
   }

   println xml

