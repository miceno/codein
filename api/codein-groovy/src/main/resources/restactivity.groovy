
import org.lpny.groovyrestlet.GroovyRestlet
import java.io.File

String logFileName= 'activitystream.log'
System.setProperty("socialcoding.log.filename", logFileName)

String script= '''

import es.tid.socialcoding.SocialCodingConfig

def config= SocialCodingConfig.newInstance().config

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.restlet.resource.StringRepresentation
import org.restlet.data.MediaType
import org.restlet.data.Status
import org.restlet.Router;  

import es.tid.socialcoding.dao.*
import es.tid.socialcoding.rest.*

Logger log= Logger.getLogger( getClass().getName())
Level log_level= Level.toLevel( config.root.log_level.toString())

    log.setLevel( log_level)                                  
    log.info "Log level set to ${log_level}"                  

final Integer PORT=config.rest.activity.port

// Create Database Connection
def db= new DbHelper().db

Router r
builder.component{
    current.servers.add(protocol.HTTP, PORT)
    // The REST Application with an initial URI
    application(uri:"/socialcoding"){
        r= router{ }
        r.attach( "/activity", ActivityStreamResource.class)
        r.attach( "/activity/{domain}/{uuid}", ActivityStreamResource.class)
    }
}.start()

'''

InputStream is = new ByteArrayInputStream(script.getBytes("UTF-8"));

gr= new GroovyRestlet()
gr.build( is)
