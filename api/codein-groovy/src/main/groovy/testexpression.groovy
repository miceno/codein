
import ExpressionContainer

// An example of how to configure the CliBuilder to read command line arguments in Groovy.

def cli = new CliBuilder( usage: 'groovy test' )

// Option help
cli.h(longOpt: 'help', 'usage information')

// Option script
cli.s(argName:'script', longOpt:'script', required: true,
      args: 1, 'Script filename')

def opt = cli.parse(args)
if (!opt) return
if (opt.h) {
   cli.usage()
   return null
}

def container= new ExpressionContainer( opt.s)

