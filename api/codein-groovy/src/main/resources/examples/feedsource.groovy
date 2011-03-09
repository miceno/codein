           
           
// Read feed

import groovy.xml.dom.DOMCategory
          
String url= "data.xml" 
feed = groovy.xml.DOMBuilder.parse(
    new InputStreamReader( new File( url).toURL().openStream()),
    false,
    false)
    
doc = feed.documentElement

/**
 * createMapIndex   This operation allows iterating an object that is not a Groovy collection nor map and
 *                  build a list of elements. The iterator is an number index iterator from 0 or 1 to the size of 
 *                  the collection
 * @param bag       The input data collection
 * @param iterator  The name of a method of the data collection used to iterate
 */
def createMapIndex( bag, iterator) {
    def attributeMap= [:]
    (0..<bag.size()).each{
        // Get the item
        def n= bag.invokeMethod( iterator, it)
        println "attribute $it: ${n}" 
        
        // Get the fields of the item and build a map entry
        attributeMap += [ (n.nodeName): n.nodeValue ]
    }
    return attributeMap
}

/**
 * Get all the metadata of a feed
 * @param doc       DocumentElement
 * @return List     A list of entries, where is entry is a map like:
 *                  [ name: "name", attributes: [att1: "value1"], text: "text"]
 */
def getMetadata( def doc){

    use( DOMCategory){
        doc.'*'.findAll{ ! ( it.nodeName == "entry") }.collect{ node->
            println "linea: " + node
            def a= node.attributes
            println "number of attributes: " + a.size()
            def map= [:]
            (0..<a.size()).each{ index -> 
                            def n= a.item( index)
                            println "attribute $index: ${n}" 
                            map += [ (n.nodeName): n.nodeValue ]
                        }
            println "attributeMap= ${map}"
            println "fin".center( 20, '*')
            [ name: node.nodeName, attributes: map, text: node.text()]
        }
        // doc.each{ println it.name() }
    }
}

def r= getMetadata( doc)
println "resultado".center( 40, '-')
r.each{ 
    println it.collect{ k, v -> """ "$k": "$v" """ }.join( ',')
}
