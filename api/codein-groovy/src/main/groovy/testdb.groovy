
import groovy.sql.*
import java.io.File
import org.apache.log4j.PropertyConfigurator
import org.apache.log4j.*

PropertyConfigurator.configure(new File('log4j.properties').toURL())
Logger log= Logger.getLogger( getClass().getName())

import es.tid.socialcoding.dao.*
import es.tid.socialcoding.SocialCodingConfig

// Create EntryFeedDAO
def helper= new DbHelper()
def entryTable= helper.db.dataSet( 'Entry')


// Closure for the list of users
def listUserActivity= { unUsuario->
    
    println "unUsuario $unUsuario"

    if( false) 
    {
       println "probing eachRow".center( 40 , "-")
       helper.db.eachRow( "select * from Entry where ownerId = $unUsuario") { 
           println it 
       }
    }
    else
    {
       println "probing findAll".center( 40 , "-")
       def userEntries= entryTable.findAll{ it.ownerId == /unUsuario.usuario/ }
       println "despues de findall"
       println "SQL= ${userEntries.sql}"
       println "Parameter= ${userEntries.parameters}"
       userEntries.each{ println it }
    }

}

   println "# argumentos =  ${args.size()}"
def theuser= [:]
   theuser.usuario= args[ 0]
   args.each{ println "argumento $it" }
   listUserActivity.call( theuser)
