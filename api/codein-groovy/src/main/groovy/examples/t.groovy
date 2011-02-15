
import java.net.URLEncoder
import org.apache.commons.lang.StringEscapeUtils


String prueba= "<name>holaáéíóúñ</name>"

def builder = new groovy.xml.MarkupBuilder()  // construct a builder step(1)

String codec= URLEncoder.encode( prueba, "UTF-8")
String escapeHtml= "España Sánchez Jörgen"

def escapeClosureBuilder= { b, text->  b.mkp.yieldUnescaped( StringEscapeUtils.escapeXml(text)) }
def escape= escapeClosureBuilder.curry( builder)
   
def xml=   builder.html{


     texto prueba
     codificadohtml { mkp.yieldUnescaped( escapeHtml) }
//     codificadohtml { escapeClosure( escapeHtml) }
     codificadohtml { escape( escapeHtml) }
     codificadohtml { escape( prueba) }
     codificadohtml prueba
//     codificadourlencoder { mkp.yield( codec )}
   }

   println xml

