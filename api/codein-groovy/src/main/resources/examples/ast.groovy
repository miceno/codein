
import org.codehaus.groovy.ast.builder.*
import org.codehaus.groovy.ast.*

   variable= "miceno"
Closure c = { it.ownerId == "$variable" }
Closure c1= { it.ownerId == "miceno" }

   println "por terminar"

AstBuilder builder = new AstBuilder()
List<ASTNode> nodes = builder.buildFromSpec {
    block {
        returnStatement {
            constant "Hello"
        }
    }
}
  nodes.each { println it.getText() } 

def value= "miceno"
def filtro = builder.buildFromCode { Closure c3= c } 
  filtro.each { println it.getText() } 


def astfromstring= builder.buildFromString( '{it->it.ownerId == variable}')
  astfromstring.each { println it.getText() } 

