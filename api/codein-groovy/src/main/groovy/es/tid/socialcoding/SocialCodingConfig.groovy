package es.tid.socialcoding

import org.apache.log4j.Logger

class SocialCodingConfig{
    def config

    private static def instance_
    final private static configFile_= "socialcoding.config"

    static def newInstance(){
         return instance_ ?: (instance_= new SocialCodingConfig( configFile_))
    }
 
    SocialCodingConfig( configFile= "socialcoding.config"){
        config = new ConfigSlurper().parse(new File( configFile).toURL())
        Logger.getLogger( getClass().getName()).debug( "configuration: $config")
    }
}

