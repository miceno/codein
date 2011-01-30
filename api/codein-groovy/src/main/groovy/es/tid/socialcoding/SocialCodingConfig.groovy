package es.tid.socialcoding

class SocialCodingConfig{
    def config

    private static def instance_
    final private static configFile_= "socialcoding.config"

    static def newInstance(){
         return instance_ ?: (instance_= new SocialCodingConfig( configFile_))
    }
 
    SocialCodingConfig( configFile){
        config = new ConfigSlurper().parse(new File( configFile).toURL())
    }
}

