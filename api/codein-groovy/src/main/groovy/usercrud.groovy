
// import org.restlet.ext.atom.*
import org.lpny.groovyrestlet.GroovyRestlet
import java.io.File


String logFileName= 'usercrud.log'
System.setProperty("socialcoding.log.filename", logFileName)


String script= '''

import org.apache.log4j.PropertyConfigurator
PropertyConfigurator.configure(new File('log4j.properties').toURL())

import org.apache.log4j.Logger
import org.restlet.resource.StringRepresentation
import org.restlet.data.MediaType
import org.restlet.data.Status

import es.tid.socialcoding.dao.*
import es.tid.socialcoding.SocialCodingConfig

Logger log= Logger.getLogger( getClass().getName())
def config= SocialCodingConfig.newInstance().config

final Integer PORT=config.rest.user.port

// Create UserFeedDAO
def database= new DbHelper().db
def userTable= new UserDAO( db: database)

// Add a new user to our database
def addHandle = { req,resp->
    newUser= req.attributes.get( 'user')
    newDomain= req.attributes.get( 'domain')

    // Set initial message
String strRepresentation= 'adding user: ' + newUser
    log.debug( strRepresentation)
    log.debug( "Req attributes".center( 40, '-'))
    req.attributes.each{ log.debug( "attribute:" + it) }

    log.debug( "Req params".center( 40, '-'))

def form = req.getEntityAsForm()
    log.debug "Form: " + form
def mapa= form.getValuesMap()
    mapa.each{ 
         key, value -> log.debug( "params: $key = $value" ) 
    }

    // TODO: Check if user already exists
def checkUserQuery= [ UUID:newUser, domain:newDomain ]
    log.debug( "Query $checkUserQuery")
    if( !userTable.findBy( checkUserQuery).size() )
    {
       // User does not exist
       log.info( "adding User $newUser")
       // Dar una respuesta en JSON
       userTable.create( [ UUID:   newUser,
                           domain: newDomain,
                           urls:   mapa.get( 'urls', "")
                         ] )
    }
    else{
       // User exists 
       log.info( "updating User $newUser")
       def conditionStmt =[ UUID:   newUser,
                            domain: newDomain]
       def updateUserStmt=[ urls:   mapa.get( 'urls', "")]
       userTable.update( updateUserStmt, conditionStmt)
    }

    resp.setEntity( 
        new StringRepresentation( strRepresentation, MediaType.TEXT_HTML ))
    resp.setStatus( Status.SUCCESS_OK)
}

// List of all users
def listHandle = { req,resp->
def type = "All"
Map filtro= [:]
def writer = new StringWriter()
def builder= new groovy.xml.MarkupBuilder(writer)

    theDomain= req.attributes.get( 'domain')
    if( theDomain)
    {  
       type= "Domain $theDomain"
       filtro += [ domain: theDomain ]
    }

    log.debug "Listing -> $type"

    builder.html{
      head { title "$type" }
      body {
          h1 "$type Users"
          p "This is the list of all the $type users available"
          ul { userTable.findBy(filtro).each{ li "${it.domain}:${it.UUID}:${it.urls}\\n" } }
      }
    }
    log.debug( "listado= ${writer.toString()}")
    resp.setEntity( new StringRepresentation( writer.toString(), MediaType.TEXT_HTML) )
    resp.setStatus( Status.SUCCESS_OK)
}

builder.component{
    current.servers.add(protocol.HTTP, PORT)
    // The REST Application with an initial URI
    application(uri:"/socialcoding"){
        router{
            // a list of all users
            restlet(uri:"/user", handle: listHandle )
            // The add a user
            restlet(uri:"/user/{domain}", handle: listHandle)
            restlet(uri:"/user/{domain}/{user}", handle: addHandle)
        }
    }
}.start()

'''

InputStream is = new ByteArrayInputStream(script.getBytes("UTF-8"));

gr= new GroovyRestlet()
gr.build( is)
