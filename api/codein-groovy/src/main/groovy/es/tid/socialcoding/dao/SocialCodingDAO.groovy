package es.tid.socialcoding.dao

/**
 * SocialCodingDAO: DAO for SocialCoding persistence
 * Implements a CRUD interface
 * In addition to the CRUD operations, DataAccessObject uses the 
 * structural information that its subclasses provide through their class 
 * names and the getFields method to build the DAOsâ meta information 
 * in a general way.
 * Subclasses are expected to follow the naming convention of MyTableDAO 
 * for a table of name MyTable. 
 * Their getFields method is expected to return a list of strings, 
 * alternating between the field names and their SQL type descriptions.
 * For instance:
 *      List getFields() { 
 *          return [
 *              'firstname', 'VARCHAR(64)', 
 *              'lastname', 'VARCHAR(64)', 
 *              'dateOfBirth', 'DATE'
 *          ]
 *      }
 */

import org.apache.log4j.Logger

abstract class SocialCodingDAO { 
    def db
    List fields = []
    List getFields() { fields.size()? fields: init()}

    private log= Logger.getLogger( getClass().getName())

    def dataSet()    { db.dataSet(tablename) } 
    def getIdField() { tablename.toLowerCase() + 'Id' } 
    def getWhereId() { "WHERE $idField = ?"}
    def init(){
       fields= db.rows('describe '+tablename).collect{ it }
       log.debug "Fields: $fields"
    }

    def sanitizeFields( args){
        List argfields= args.keySet().toList()
        List intersection= getFieldNames().intersect( argfields)
        List oddFields= argfields - intersection
        if( oddFields.size())
            log.debug "Odd Fields in operation: $oddFields"
        return args.subMap( intersection)
    }

    String getTablename() { 
        def name = this.getClass().name 
        return name[name.lastIndexOf('.')+1..-4]
    }

    Map getSchema() {
        Map result = [:] 
        fieldNames.each {result[it] = fields[fields.indexOf(it)+1]} 
        return result
    } 

    List getFieldNames() {
        List result = [] 
        return fields*.Field
    }

    def create(Map args) {
        args= sanitizeFields( args)
        dataSet().add args
    } 

    private def composeFieldStmt = { k, v -> "$k = '$v'"}

    def update(id, args) {
        args= sanitizeFields( args)
        setStmt= args.collect( composeFieldStmt).join( ',')
        stmt   = "UPDATE $tablename SET $setStmt $whereId" 
        log.debug( "DAO update: $stmt")
        db.executeUpdate stmt, [ id]
    } 

    def delete(id) {
        def stmt = "DELETE FROM $tablename $whereId" 
        log.debug( "DAO delete: $stmt")
        db.executeUpdate stmt, [id]
    }

    def findBy( def args){
        def selects = fieldNames + idField
        args= sanitizeFields( args)
        def result=[]
        def whereStmt = args.collect( composeFieldStmt).join( ' and ')
        def stmt = "SELECT " + selects.join(',') +
                   " FROM $tablename WHERE $whereStmt"
        log.debug( "DAO findBy: $stmt")
        db.eachRow(stmt.toString()){ rs -> 
            Map businessObject = [:] 
            selects.each { businessObject[it] = rs[it] } 
            result << businessObject
        }
        log.debug "FindBy: result = $result"
        return result
    }

    def find(id){
        def selects = fieldNames + idField
        def stmt = "SELECT " + selects.join(',') +
                   " FROM $tablename $whereId"
        log.debug( "DAO find: $stmt")
        def result= db.firstRow( stmt.toString(), [id])
        log.debug "Find: result = $result"
        return result
    }

    def all(sortField) {
        def selects = fieldNames + idField 
        def result = [] 
        def stmt = "SELECT " + selects.join(',') +
                   " FROM $tablename ORDER BY $sortField"
        db.eachRow(stmt.toString()){ rs -> 
            Map businessObject = [:] 
            selects.each { businessObject[it] = rs[it] } 
            result << businessObject
        } 
        return result
    }

}
