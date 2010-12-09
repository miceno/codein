//
//  jms-producer
//
//  Created by Orestes Sanchez on 2010-12-06.
//  Copyright (c) 2010 Telefónica I+D. All rights reserved.
//

package es.tid.socialcoding.producer;

import es.tid.socialcoding.CodeinJMS

import groovy.jms.JMS
import javax.jms.*
import javax.jms.Message


/**
 * Class
 *
 */
public class Producer extends CodeinJMS
{
         
    public Producer( String configFile) {
        super( configFile)
        db = [  url         : config.bd.url,
                user        : config.bd.user,
                password    : config.bd.password,
                driver      : config.bd.driver]
        
    } 
         
    //Mysql Configuration
    def  db
    
}

