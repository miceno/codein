
import org.lpny.groovyrestlet.GroovyRestlet
import java.io.File

import org.apache.log4j.PropertyConfigurator


PropertyConfigurator.configure(new File('log4j.properties').toURL())

def cli = new CliBuilder( usage: 'groovy launcher' )

cli.h(longOpt: 'help', 'usage information')
cli.s(argName:'script', longOpt:'script', required: true,
      args: 1, 'Script filename')

def opt = cli.parse(args)
if (!opt) return
if (opt.h) {
   cli.usage()
   return null
}


final String FILESCRIPT= opt.s
String ROOT = "./src/main/groovy/es/tid/socialcoding/"

def s=new File(ROOT, FILESCRIPT).toURI()

println "fichero: $s"

gr= new GroovyRestlet()
gr.build( s)

String script= """

"""

InputStream is = new ByteArrayInputStream(text.getBytes("UTF-8"));

