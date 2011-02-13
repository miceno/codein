

import es.tid.socialcoding.consumer.*

class TestProcessor{


      TestProcessor(){
          String logFilename= getClass().getName() + ".log"
          System.setProperty("socialcoding.log.filename", logFilename)
      }

      def testCreateTask(){
          def f = new FeedProcessor()

          def testTask= [ 
                          UUID: 'orestes', 
                          domain: 'bluevia', 
                          url: 'http://www.mailinator.com/atom.jsp?email=oronegro'
          ]

          println "processing $testTask"
          f.processTask( testTask)
      }

      def testUpdateTask(){
          def f = new FeedProcessor()

          def testTask= [ 
                          UUID: 'orestes', 
                          domain: 'bluevia', 
                          url: 'http://www.mailinator.com/atom.jsp?email=oronegro'
          ]

          (1..2).each{ 
              println "processing $it: $testTask"
              f.processTask( testTask)
          }
      }

      def testNullTask(){
          def f = new FeedProcessor()
          def testTask= null
          println "processing $testTask"
          f.processTask( testTask)
      }

}

def t= new TestProcessor()
    t.class.methods.findAll{ 
        it.name.contains( 'test') 
    }.each{ println "Testing " + it.name; t.invokeMethod( it.name, null) }
