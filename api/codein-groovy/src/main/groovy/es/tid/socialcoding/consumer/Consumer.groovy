//
//  jms-producer
//
//  Created by Orestes Sanchez on 2010-12-06.
//  Copyright (c) 2010 Telefónica I+D. All rights reserved.
//

package es.tid.socialcoding.consumer

import es.tid.socialcoding.CodeinJMS
import groovy.jms.*
import javax.jms.*
import javax.jms.Message


/**
 * Class
 *
 */
public class Consumer extends CodeinJMS
{
          
    public Consumer( String queueUrl) {
        super( queueUrl)        
    } 
    
}

