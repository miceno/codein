
import org.apache.log4j.*

def logConfig = new ConfigSlurper().parse( new File( 'log4j.config').toURL()
)
//   logConfig.log4j."appender.LOGFILE.file"=getClass().getName() + ".log"

   println "start".center( 40, "-")
   logConfig.each{ println it }

   println "properties".center( 40, "-")
   logConfig.toProperties( ).each{ println it }

PropertyConfigurator.configure( logConfig.toProperties())

Logger log= Logger.getLogger( getClass().getName())


   log.debug( "probando")
