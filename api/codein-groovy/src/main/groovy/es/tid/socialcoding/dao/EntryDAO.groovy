package es.tid.socialcoding.dao

class EntryDAO extends SocialCodingDAO {
    List getFields() { return [
          'date', 'datetime', 
          'body', 'text', 
          'userId', 'int(11)', 
          ]
    }
}

