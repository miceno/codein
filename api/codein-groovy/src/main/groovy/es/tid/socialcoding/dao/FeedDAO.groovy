package es.tid.socialcoding.dao

class FeedDAO extends SocialCodingDAO {
    List getFields() { return [
          'name', 'varchar(255)', 
          'url', 'tinytext', 
          'frequency', 'int(10)', 
          ]
    }
}

