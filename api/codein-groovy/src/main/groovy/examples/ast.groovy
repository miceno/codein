
import org.codehaus.groovy.ast.builder.*
import org.codehaus.groovy.ast.*

   variable= "miceno"
Closure c = { it.ownerId == "$variable" }
Closure c1= { it.ownerId == "miceno" }

   println "por terminar"
