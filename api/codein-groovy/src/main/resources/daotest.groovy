
import org.apache.log4j.Logger

import es.tid.socialcoding.dao.SocialCodingDAO
import es.tid.socialcoding.dao.DbHelper
import es.tid.socialcoding.dao.EntryDAO as oldEntryDAO

String logFileName= "dao.log"
System.setProperty("socialcoding.log.filename", logFileName)

class EntryDAO extends SocialCodingDAO{
}

Logger log= Logger.getLogger( getClass().getName())


helper= new DbHelper()
 table= new EntryDAO(db: helper.db)
 def theFieldNames = [
         "entryId", 
         "id", 
         "authorId", 
         "authorLink", 
         "title", 
         "link", 
         "updated",
         "published", 
         "content", 
         "source", 
         "ownerId", 
         "ownerDomain" ]

/*
 log.debug "getFields".center( 40, '-')
 table.getFields().each{ log.debug it }
 // Test: getFields
 assert table.getFields()*.Field == theFieldNames


 log.debug "getFieldNames".center( 40, '-')
 table.getFieldNames().each{ log.debug it }
 // Test: getFieldNames

 assert table.getFieldNames() == theFieldNames

def consulta= [ ownerDomain: "bluevia", pepito: "palotes" ]
 log.debug "consulta ".center( 40, '-')
 consulta.each{ log.debug it }

def saneConsulta= table.sanitizeFields( consulta)
 log.debug "consulta sana".center( 40, '-')
 saneConsulta.each{ log.debug it }
 // Test: sanitizeFields
 assert saneConsulta == [ ownerDomain: "bluevia" ]

 result = table.findBy( consulta).each{ log.debug it }
 // Test: findBy
 assert result[0].keySet().toList()== theFieldNames

 result = table.findBy( ).each{ log.debug it }
 // Test: findBy
 assert result[0].keySet().toList()== theFieldNames
*/


def newRecord = [ ownerId: "uuuuuu", ownerDomain: "dddddd" ]

 // Test: delete records
 table.delete( newRecord)

 // Test: create
 table.create( newRecord)
 assert table.findBy( newRecord).size() == 1

 // Test: update 
def updateRecord = [:]
 updateRecord.putAll( newRecord)
 updateRecord += [ title: "Mi titulo", id: "http://www.tid.es" ]
 table.update( updateRecord, newRecord)
 assert table.findBy( newRecord)[0].title == updateRecord.title
  
 // Test: delete
 table.delete( newRecord)
