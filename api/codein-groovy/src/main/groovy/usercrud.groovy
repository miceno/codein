
import org.restlet.ext.atom.*
import org.lpny.groovyrestlet.GroovyRestlet
import java.io.File

import org.apache.log4j.PropertyConfigurator


PropertyConfigurator.configure(new File('log4j.properties').toURL())

String script= '''

import org.apache.log4j.PropertyConfigurator
import org.apache.log4j.Logger
import org.restlet.resource.StringRepresentation
import org.restlet.data.MediaType
import org.restlet.data.Status

import es.tid.socialcoding.dao.*

final Integer PORT=8183

Logger log= Logger.getLogger( getClass().getName())

// Create UserFeedDAO
def helper= new DbHelper()
def userTable= helper.db.dataSet( 'User')

// Add a new user to our database
def addHandle = { req,resp->
    log.debug( 'adding a new user')
    log.debug( 'User = ' + req.attributes.get( 'user') )
    log.debug( "Req attributes".center( 40, '-'))
    req.attributes.each{ log.debug( "attribute:" + it) }

    log.debug( "Req params".center( 40, '-'))

def form = req.getEntityAsForm()
    log.debug "Form: " + form
def mapa= form.getValuesMap()
    log.debug( "mapa de valores= $mapa")
    mapa.each{ 
         key, value -> log.debug( "params: $key = $value" ) 
    }

    // Set status to OK
    resp.setStatus( Status.SUCCESS_OK)

    // Set initial message
String strRepresentation= 'adding user: ' + req.attributes.get( 'user')

    // Check if user is same on URI and on parameters
    if( mapa.get( 'user') != req.attributes.get( 'user')) 
    {
        // ERROR: change the representation and the status code
        strRepresentation += "bad user request ${mapa.get( 'user')} != ${req.attributes.get( 'user')}"
        resp.setStatus( Status.CLIENT_ERROR_BAD_REQUEST)
    }

    // Check if domain is same on URI and on parameters
    if( mapa.get( 'domain') != req.attributes.get( 'domain')) 
    {
        // ERROR: change the representation and the status code
        strRepresentation += "bad domain request ${mapa.get( 'domain')} != ${req.attributes.get( 'domain')}"
        resp.setStatus( Status.CLIENT_ERROR_BAD_REQUEST)
    }

    if( resp.getStatus() == Status.SUCCESS_OK)
    {
       log.debug( "adding data to User table")
       // Dar una respuesta en JSON
       userTable.add( UUID:   mapa.get( 'user'), 
                      domain: mapa.get( 'domain'),
                      urls:   mapa.get( 'urls'))
    }

    resp.setEntity( 
        new StringRepresentation( strRepresentation, MediaType.TEXT_HTML ))
}

// List of all users
def listHandle = { type,req,resp->
    log.debug 'Listing -> ' + type
def writer = new StringWriter()
def html = new groovy.xml.MarkupBuilder(writer)
    html.html{
      head { title "$type" }
      body {
          h1 "$type Users"
          p "This is the list of all the $type users available"
          ul { userTable.each{ li "${it.domain}:${it.UUID}\\n" } }
      }
    }
    log.debug( "listado= ${writer.toString()}")
    resp.setEntity( new StringRepresentation( writer.toString(), MediaType.TEXT_HTML) )
    resp.setStatus( Status.SUCCESS_OK)
}

def listDomainHandle= listHandle.curry( 'Domain')
def listUsersHandle= listHandle.curry( 'All')

builder.component{
    current.servers.add(protocol.HTTP, PORT)
    // The REST Application with an initial URI
    application(uri:"/user"){
        router{
            // a list of all users
            restlet(uri:"", handle: listUsersHandle )
            // The add a user
            restlet(uri:"/{domain}", handle: listDomainHandle)
            restlet(uri:"/{domain}/{user}", handle: addHandle)
        }
    }
}.start()

'''

InputStream is = new ByteArrayInputStream(script.getBytes("UTF-8"));

gr= new GroovyRestlet()
gr.build( is)
