package es.tid.socialcoding

import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.log4j.PropertyConfigurator
import groovy.util.logging.Log4j


@Log4j
class SocialCodingConfig{
    def config

    private static def instance_
    final private static configFile_= "socialcoding.config"

    static def newInstance(){
         return instance_ ?: (instance_= new SocialCodingConfig( configFile_))
    }
 
    SocialCodingConfig( configFile= "socialcoding.config"){
        config = new ConfigSlurper().parse(new File( configFile).toURL())
        PropertyConfigurator.configure(new File('log4j.properties').toURL())
        Logger.getLogger( getClass().getName()).debug( "configuration: $config")
    }
    
    def reload(){
        config = new ConfigSlurper().parse( config.configFile )
        Level log_level= Level.toLevel( config.root.log_level.toString())
        Logger.getRootLogger().setLevel( log_level)
        log.info "Log level set to ${log_level}"
        
    }
}

