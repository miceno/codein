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

import groovy.util.logging.Log4j

@Log4j
abstract class SocialCodingDAO { 
    def db
    private List fields = []
    Integer offset= 0
    Integer limit= Integer.MAX_VALUE
    
    SocialCodingDAO( db= null) { init() }

    List getFields() { fields.size()? fields: init()}

    def dataSet()        { db.dataSet(tablename) } 
    def getIdField()     { tablename.toLowerCase() + 'Id' } 
    def getWhereId()     { "WHERE $idField = ?"}
    def getLimitClause() { "LIMIT $offset, $limit" }
    
    def getWhere( def condition){
        if( ! condition?.size()) 
            return [ where: "", params: []]
        condition= sanitizeFields( condition)
        [ 
        where: "WHERE " + condition.collect( composeFieldStmt).join( ' and '), 
        params: condition.values().toList()
        ]
    }
    
    def init(){
       if ( !db) db= new DbHelper().db;
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
        log.debug( "INSERT $args")
        try{
            dataSet().add args
        }catch( e){
            log.error "Error executing INSERT: $args"
            log.error e
        }
    } 

    private def composeFieldStmt = { k, v -> "$k = ?"}

    def update( newValues, condition) {
        def whereStmt= getWhere( condition)
        log.debug( "DAO update WHERE: ${whereStmt}")

        newValues= sanitizeFields( newValues)
        def setStmt= newValues.collect( composeFieldStmt).join( ',')
        def setValues= newValues.values().toList()
        log.debug( "DAO update SET: $setStmt, parameters $setValues")
        String stmt   = "UPDATE $tablename SET $setStmt ${whereStmt?.where}" 

        def params= (setValues + whereStmt?.params)
        log.debug( "DAO update: $stmt, parameters $params")
        try{
            db.executeUpdate stmt, params
        }catch( e){
            log.error "Error executing UPDATE: $stmt, parameters $params"
            log.error e
        }
    } 

    def delete(String id) {
        String stmt = "DELETE FROM $tablename $whereId" 
        log.debug( "DAO delete: $stmt")
        db.executeUpdate stmt, [id]
    }

    def delete(Map condition) {
        def whereStmt= getWhere( condition)
        if( ! (whereStmt?.where?.size()) )
        {
            log.error "Trying to delete all records from a table"
            return 0
        }
        String stmt = "DELETE FROM $tablename ${whereStmt?.where}" 
        log.debug( "DAO delete from map: $stmt")
        db.executeUpdate stmt, whereStmt?.params
    }

    def findBy( condition= [:]){
        def whereStmt = getWhere( condition)
        log.debug( "DAO findBy WHERE: ${whereStmt}")
        
        def selects = fieldNames + idField
        def result= []
        def l= getLimitClause()
        def stmt = "SELECT " + selects.join(',') +
                   " FROM $tablename ${whereStmt?.where} $l"
        log.debug( "DAO findBy: $stmt, parameters ${whereStmt?.params}")
        List params= whereStmt?.params
        
        db.eachRow(stmt.toString(), params){ rs -> 
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

    def findAll(condition, sortField) {
        def selects = fieldNames + idField 
        def result = [] 
        def l= getLimitClause()
        def stmt = "SELECT " + selects.join(',') +
                   " FROM $tablename ORDER BY $sortField $l"
        db.eachRow(stmt.toString()){ rs -> 
            Map businessObject = [:] 
            selects.each { businessObject[it] = rs[it] } 
            result << businessObject
        } 
        return result
    }

}
