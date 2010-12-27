package es.tid.socialcoding.dao

import groovy.sql.Sql 
import groovy.text.SimpleTemplateEngine  as STE  

import es.tid.socialcoding.*

class DbHelper  {
    // Constructor from DataSource for Injection
    Sql db  
    DbHelper(source)  {
        // Get global config
        def config= SocialCodingConfig.newInstance().config 
        
        // def source =  new org.hsqldb.jdbc.jdbcDataSource()
        source.database = config.db.url
        source.user = config.db.user
        source.password = config.db.password
        db = new Sql(source)
        }

    // Constructor 
    DbHelper()  {
        def config= SocialCodingConfig.newInstance().config 

        db= Sql.newInstance( config.db.url.toString(),
    							config.db.username.toString(),
    							config.db.password.toString(),
    							config.db.driver.toString())
    	
    }
}
