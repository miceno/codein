
import org.restlet.data.MediaType;
import org.restlet.resource.DomRepresentation;


DomRepresentation representation = new DomRepresentation( MediaType.TEXT_XML);

def builder=groovy.xml.DOMBuilder.newInstance( false, false)

def dom= builder.html{
       body{
           10.times{
             h1 "titulo $it"
             p "atendiendo a mi parrafo numero $it"
           }
       }
}

// dom.metaClass.methods.each { println it}

println "dom class = ${dom.getClass().getName()}"
def document= dom.getOwnerDocument()
println "document class = ${document.getClass().getName()}"

use( groovy.xml.dom.DOMCategory){
    println document.'*'.size()
    def body = document.html.body
    body.each{ println it }

}

representation.setDocument( document)
def newdoc= representation.getDocument( )
println "new document class = ${newdoc.getClass().getName()}"

println "DOM".center( 40, "*")
// println dom

println "representation".center( 40, "*")
representation.write( new File( 'representation.dom').newOutputStream())

