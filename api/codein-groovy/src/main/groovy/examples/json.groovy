

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

def json = """
{
  "after": "8a0f13db2cf20b7ce57b54062cca9d10a302fe3b", 
  "before": "5501b179e5cfea58d89319bb1091f43db10883d5", 
  "commits": [
    {
      "added": [], 
      "author": {
        "email": "orestes@acm.org", 
        "name": "miceno", 
        "username": "miceno"
      }, 
      "id": "2806f58c88dbded9fd7f0b28f56d5232e4224765", 
      "message": "[NEW]: script to create commits for push web tests.", 
      "modified": [
        "api/codein-groovy/src/main/groovy/examples/docommit.sh"
      ], 
      "removed": [], 
      "timestamp": "2011-02-15T08:21:53-08:00", 
      "url": "https://github.com/miceno/codein/commit/2806f58c88dbded9fd7f0b28f56d5232e4224765"
    }, 
    {
      "added": [
        "api/codein-groovy/src/main/groovy/examples/prueba.2011-02-15-17:22:35"
      ], 
      "author": {
        "email": "orestes@acm.org", 
        "name": "miceno", 
        "username": "miceno"
      }, 
      "id": "24c94d35972ce7add3e581144b4a4dd7e9fe2f71", 
      "message": "[TEST]: Test run at 2011-02-15-17:22:35 #codein", 
      "modified": [], 
      "removed": [], 
      "timestamp": "2011-02-15T08:22:35-08:00", 
      "url": "https://github.com/miceno/codein/commit/24c94d35972ce7add3e581144b4a4dd7e9fe2f71"
    }, 
    {
      "added": [
        "api/codein-groovy/src/main/groovy/examples/prueba.2011-02-15-17:22:36"
      ], 
      "author": {
        "email": "orestes@acm.org", 
        "name": "miceno", 
        "username": "miceno"
      }, 
      "id": "b4b4fd6bc86314cadd26f98b878f357f3ee726e7", 
      "message": "[TEST]: Test run at 2011-02-15-17:22:36 #codein", 
      "modified": [], 
      "removed": [], 
      "timestamp": "2011-02-15T08:22:36-08:00", 
      "url": "https://github.com/miceno/codein/commit/b4b4fd6bc86314cadd26f98b878f357f3ee726e7"
    }, 
    {
      "added": [
        "api/codein-groovy/src/main/groovy/examples/prueba.2011-02-15-17:22:37"
      ], 
      "author": {
        "email": "orestes@acm.org", 
        "name": "miceno", 
        "username": "miceno"
      }, 
      "id": "2d5ffb0e0492f9cf111880603ceb7a58b2fdb7a4", 
      "message": "[TEST]: Test run at 2011-02-15-17:22:37 #codein", 
      "modified": [], 
      "removed": [], 
      "timestamp": "2011-02-15T08:22:37-08:00", 
      "url": "https://github.com/miceno/codein/commit/2d5ffb0e0492f9cf111880603ceb7a58b2fdb7a4"
    }, 
    {
      "added": [], 
      "author": {
        "email": "orestes@acm.org", 
        "name": "miceno", 
        "username": "miceno"
      }, 
      "id": "8a0f13db2cf20b7ce57b54062cca9d10a302fe3b", 
      "message": "[TEST] #codein", 
      "modified": [], 
      "removed": [
        "api/codein-groovy/src/main/groovy/examples/prueba.2011-02-15-17:22:35"
      ], 
      "timestamp": "2011-02-15T08:23:28-08:00", 
      "url": "https://github.com/miceno/codein/commit/8a0f13db2cf20b7ce57b54062cca9d10a302fe3b"
    }
  ], 
  "compare": "https://github.com/miceno/codein/compare/5501b17...8a0f13d", 
  "forced": false, 
  "pusher": {
    "email": "orestes@acm.org", 
    "name": "miceno"
  }, 
  "ref": "refs/heads/master", 
  "repository": {
    "created_at": "2010/08/10 03:14:19 -0700", 
    "description": "Social coding proof of concept", 
    "fork": false, 
    "forks": 1, 
    "has_downloads": true, 
    "has_issues": true, 
    "has_wiki": true, 
    "homepage": "http://serotonin.tid.es", 
    "language": "Groovy", 
    "name": "codein", 
    "open_issues": 0, 
    "owner": {
      "email": "orestes@acm.org", 
      "name": "miceno"
    }, 
    "private": false, 
    "pushed_at": "2011/02/15 08:23:33 -0800", 
    "size": 4008, 
    "url": "https://github.com/miceno/codein", 
    "watchers": 2
  }
}
""" 


def builder = new groovy.json.JsonBuilder()

    builder.person{
        table{
            element "hola"
            commit{
                message "c:/hola/adios\\hola"
            }
        }
    }
    
    println builder.toString()

def root = new JsonSlurper().parseText(json)

def ETIQUETA = "#codein"
def TAG = /.*${ETIQUETA}?/

// Get all messages that matches the tag
    println "Messages".center( 40, '*')
    root.commits.message.each{ println it}
    println "Messages with $TAG".center( 40, '*')
    
def l= root.commits.message
    l.grep( ~TAG).each{ println it }
    
    
    def twister = 'she sells sea shells by the sea shore' // contains word 'shore' assert twister =~ 'shore' // contains 'sea' twice (two ways)
    assert (twister =~ 'sea').count == 2 
    twister.split(/ /).grep(~/sh/).each { println it }
