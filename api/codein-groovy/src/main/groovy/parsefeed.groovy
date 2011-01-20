
/**
* A consumer console that consumes all the message in a queue
*/

import static javax.xml.xpath.XPathConstants.*
import javax.xml.xpath.*
import groovy.xml.dom.DOMCategory
import groovy.xml.*
import org.apache.log4j.*
import ExpressionContainer

Logger log= Logger.getLogger( getClass().getName())

def cli = new CliBuilder( usage: 'groovy parsefeed' )

cli.h(longOpt: 'help', 'usage information')
cli.x(argName:'xpath', longOpt:'xpath', required: false,
      args: 1, 'xpath expression')
cli.f(argName:'expressions', longOpt:'expressions', required: true,
      args: 1, 'expressions file')
cli.u(argName:'url', longOpt:'url', required: false,
      args: 1, 'url of the feed')

def opt = cli.parse(args)
if (!opt) return
if (opt.h) {
   cli.usage()
   return null
}


    // A closure that applies each Expression to an element
    def apply= { listExpressions, element ->
        listExpressions.collect{ xexpression ->
            log.debug "begin parsing feed with ${xexpression}"
            def result
            use (DOMCategory) { 
                result= element.xpath( xexpression.XPATH, NODESET) 
                /* Print each element */
                log.debug "${xexpression.XPATH}: ${result.size()}= ${result.text()}"
            }
            result.text()
        }// lexpression.collect
    }
def parser= new ExpressionContainer( opt.f)
    // Curry the apply to the Parser
    def applyToElement= apply.curry( parser.expressions.values())

final String XPATH_EXPRESSION="//entry"

    // xpath=  (opt.x ?: XPATH_EXPRESSION )
    // doc = new DOMBuilder.parse( opt.u, false, false)
    doc = DOMBuilder.parse( 
              new InputStreamReader( opt.u.toURL().openStream()), 
              false, 
              false).documentElement

    use( DOMCategory){
        nodes= doc.xpath( XPATH_EXPRESSION, NODESET)

        // For each node, extract all the expressions
        result= nodes.collect( applyToElement)
        result.each{ log.debug "texto: " + it }
        result
    }// use
    
    log.debug "end parsing feed"

System.exit(0)
