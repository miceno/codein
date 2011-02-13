
import java.net.URLEncoder
import org.apache.commons.lang.StringEscapeUtils


String prueba= "<name>hola</name>"

def builder = new groovy.xml.MarkupBuilder()  // construct a builder step(1)

String codec= URLEncoder.encode( prueba, "UTF-8")
String escapeHtml= StringEscapeUtils.escapeXml( "EspaÃ±a SÃ¡nchez JÃ³rgen")

def escapeClosureBuilder= { b, text->  b.mkp.yieldUnescaped( text) }
def escape= escapeClosureBuilder.curry( builder)
   
def xml=   builder.html{


     texto prueba
     codificadohtml { mkp.yieldUnescaped( escapeHtml) }
//     codificadohtml { escapeClosure( escapeHtml) }
     codificadohtml { escape( escapeHtml) }
//     codificadourlencoder { mkp.yield( codec )}
   }

   println xml

