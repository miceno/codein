

/**
 * Transform the keys of a map into another set of keys
 */
 
 def mapping = [ authorName: "author", authorDomain: "domain"]
 
 def mapa = [
            [ test: "mapping", authorName: "orestes", authorDomain: "bluevia", relleno: 1],
            [ test: "no mapping", relleno: 1, valor: "no sé qué poner"],
            [ authorName: "duplicate mapping", authorDomain: "bluevia", domain: "bluevia-domain", relleno: 2],
            ]
       
def transform2 = { m ->

    // Using Expando's
}    

 
 /**
  * Implementation using maps
  */     
 def transform1 = { m ->
     
     println "empezando con $m"
     // Get intersection keys: keys that are in both m and mapping
     def keyIntersection= m.keySet().intersect( mapping.keySet())
     
     // Get the resulting submap
     def intersection= m.subMap( keyIntersection)
     println "intersection: $intersection"
     // Create new map from intersection
     def transformation= intersection.inject([:]){ map, entry ->
            // Insert a new transformed entry
            map.putAt( (mapping[ "${entry.key}"]), entry.value)
            println "map: $map"
            map
     }
     println "raw transformation: $transformation"

     // Preserve duplicated keys that are already in m
     // In case we would like to give the relevance to the mapping, comment this line
     transformation.keySet().removeAll( m.keySet())
     
     println "unduplicated transformation: $transformation"
     
     // Remove from intersection the keys that are already in the map
     // Remove intersection map
     m.keySet().removeAll( mapping.keySet())
     m + transformation
 }

 println "mapping: $mapping"
 println "before".center( 40, '*')     
 mapa.each{ println it }
 
 
 def mapa1= mapa.collect( transform1)
 
 println "after 1".center( 40, '*')     
 mapa1.each{ println it }
 /*
 def mapa2= mapa.collect( transform2)
 
 println "after 2".center( 40, '*')     
 println mapa2
 */