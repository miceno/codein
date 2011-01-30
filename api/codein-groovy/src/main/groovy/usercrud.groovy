
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
final Integer PORT=8183

import es.tid.socialcoding.dao.*

Logger log= Logger.getLogger( getClass().getName())

// Create UserFeedDAO
def db= new DbHelper().db
def userTable= db.dataSet( 'User')

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
String checkUserQuery="""
    select * from User where UUID='$newUser' and domain='$newDomain'
""" 
    log.debug( "Query $checkUserQuery")
    if( !db.rows( checkUserQuery).size() )
    {
       // User does not exists 
       log.debug( "adding User $newUser")
       // Dar una respuesta en JSON
       userTable.add( UUID:   newUser,
                      domain: newDomain,
                      urls:   mapa.get( 'urls', ""))
    }
    else{
       // User does not exists 
       log.debug( "updating User $newUser")
       String updateUserStm="""
          update User 
             set urls='${mapa.get( 'urls', "")}'
             where UUID='$newUser' and domain='$newDomain'
       """
       db.execute updateUserStm
    }

    resp.setEntity( 
        new StringRepresentation( strRepresentation, MediaType.TEXT_HTML ))
    resp.setStatus( Status.SUCCESS_OK)
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
    application(uri:"/socialcoding"){
        router{
            // a list of all users
            restlet(uri:"/user", handle: listUsersHandle )
            // The add a user
            restlet(uri:"/user/{domain}", handle: listDomainHandle)
            restlet(uri:"/user/{domain}/{user}", handle: addHandle)
        }
    }
}.start()

'''

InputStream is = new ByteArrayInputStream(script.getBytes("UTF-8"));

gr= new GroovyRestlet()
gr.build( is)
