
List fields= [ "id", "entryId", "ownerId", "ownerDomain" ]

Map args= [ entryId : 1,  
            id: "http://www.tid.es",  
            ownerId: "miceno",  
            pepito: "palotes",
            ownerDomain: "bluevia" ]

println "mapa inicial: $args"

keys= args.keySet()
println "keys: ${keys}"

intersect= fields.intersect( keys)
oddFields= keys- intersect

println "intersect: ${intersect}"
println "oddFields: ${oddFields}"

def sub= args.subMap( intersect)
println "sub mapa resultante: $sub"

keys.removeAll( oddFields)
println "mapa resultante: $args"


