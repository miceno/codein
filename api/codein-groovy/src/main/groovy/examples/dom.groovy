
import org.restlet.data.MediaType;
import org.restlet.resource.DomRepresentation;
import org.w3c.dom.Document


DomRepresentation representation = new DomRepresentation( MediaType.TEXT_XML);

Document doc= representation.getDocument()
def builder=new groovy.xml.DOMBuilder( doc)

def dom= builder.html{
       body{
           10.times{
             h1 "titulo $it"
             p "atendiendo a mi parrafo numero $it"
           }
       }
}

  doc.appendChild( dom)
// dom.metaClass.methods.each { println it}

println "dom class = ${dom.getClass().getName()}"

   println "debug document = " + dom.textContent

use( groovy.xml.dom.DOMCategory){
   println dom.'**'.h1.text()
}

def newdoc= representation.getDocument( )
println "new document class = ${newdoc.getClass().getName()}"

println "DOM".center( 40, "*")
println dom

println "representation".center( 40, "*")
// representation.write( new File( 'representation.dom').newOutputStream())
representation.write( System.out)

