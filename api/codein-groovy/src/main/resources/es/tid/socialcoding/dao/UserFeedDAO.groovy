package es.tid.socialcoding.dao

class UserFeedDAO extends SocialCodingDAO {
    List getFields() { return [
          'userId', 'int(11)', 
          'feedId', 'int(11)', 
          ]
    }
}

