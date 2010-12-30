package tid

/**
* A consumer console that consumes all the message in a queue
*/

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import javax.xml.xpath.*

import org.apache.log4j.*

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

Logger log= Logger.getLogger( getClass().getName())

def cli = new CliBuilder( usage: 'groovy parsefeed' )

cli.h(longOpt: 'help', 'usage information')
cli.c(argName:'configfile', longOpt:'config', required: true,
      args: 1, 'Configuration filename')
cli.x(argName:'xpath', longOpt:'xpath', required: false,
      args: 1, 'xpath expression')
cli.f(argName:'file', longOpt:'file', required: false,
      args: 1, 'xml file')

def opt = cli.parse(args)
if (!opt) return
if (opt.h) {
   cli.usage()
   return null
}

final String XPATH_EXPRESSION="//entry"
String xpath=  (opt.x ?: XPATH_EXPRESSION )

File f= new File( opt.f)
log.debug  "reading file: ${f}" 

DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
DocumentBuilder db = factory.newDocumentBuilder();
Document doc = db.parse(f);

log.debug "feed begin Text".center( 30, '=')
log.debug  doc.getDocumentElement().textContent
log.debug "feed end Text".center( 30, '=')

log.debug "feed begin dump".center( 30, '=')
log.debug  f.text
log.debug "feed end dump".center( 20, '=')

def xp = XPathFactory.newInstance().newXPath()
def xpInner = XPathFactory.newInstance().newXPath()
def expr     = xp.compile(XPATH_EXPRESSION)
def nodes    = expr.evaluate(doc, XPathConstants.NODESET)

 
def lexpression= xpath.split( ",")

    // For each node, extract all the expressions
    nodes.collect{
        element ->
        // For each expression extract data
        lexpression.collect{ xexpression ->
           log.debug "begin parsing feed with ${xexpression}"
           def result= xp.evaluate( xexpression, element)

           /* Print each element */
           log.debug "${xexpression}: ${result}"
           result
        }
    }.each{ log.debug "texto: " + it }
    
    log.debug "end parsing feed"

System.exit(0)


