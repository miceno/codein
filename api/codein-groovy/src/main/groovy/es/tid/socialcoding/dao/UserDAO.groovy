package es.tid.socialcoding.dao

class UserDAO extends SocialCodingDAO {
    List getFields() { return [
          'UUID', 'varchar(32)', 
          'domain', 'VARCHAR(255)', 
          'urls', 'text', 
          ]
    }
}

