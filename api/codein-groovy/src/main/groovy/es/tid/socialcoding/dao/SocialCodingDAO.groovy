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

abstract class SocialCodingDAO { 
    def db
    abstract List getFields()

    def dataSet()    { db.dataSet(tablename) } 
    def getIdField() { tablename.toLowerCase() + 'Id' } 
    def getWhereId() { "WHERE $idField = ?"}

    String getTablename() { 
        def name = this.getClass().name 
        return name[name.lastIndexOf('.')+1..-4]
    }

    def create(List args) {
        Map argMap = [:]
        args.eachWithIndex { arg, i -> argMap[fieldNames[i]] = arg }
        dataSet().add argMap
    } 

    Map getSchema() {
        Map result = [:] 
        fieldNames.each {result[it] = fields[fields.indexOf(it)+1]} 
        return result
    } 

    List getFieldNames() {
        List result = [] 
        0.step(fields.size(),2) { result << fields[it] } 
        return result
    }


    def update(field, newValue, id) {
        def stmt = "UPDATE $tablename SET $field = ? $whereId" 
        db.executeUpdate stmt, [newValue, id]
    } 

    def delete(id) {
        def stmt = "DELETE FROM $tablename $whereId" 
        db.executeUpdate stmt, [id]
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
