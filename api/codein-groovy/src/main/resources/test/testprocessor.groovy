

import es.tid.socialcoding.consumer.*

class TestProcessor{

    def testTaskStack= [ 
                    UUID: 'orestes', 
                    domain: 'bluevia', 
                    url: 'http://stackoverflow.com/feeds/tag/groovy'
    ]

    def testTaskMailnator= [ 
                    UUID: 'orestes', 
                    domain: 'bluevia', 
                    url: 'http://www.mailinator.com/atom.jsp?email=oronegro'
    ]

    def testTaskGitHub= [ 
                    UUID: 'orestes', 
                    domain: 'bluevia', 
                    url: 'https://github.com/miceno/codein/commits/master.atom'
    ]


      TestProcessor(){
          String logFilename= getClass().getName() + ".log"
          System.setProperty("socialcoding.log.filename", logFilename)
      }

      def testCreateTask(){
          def f = new FeedProcessor()

          def testTask= testTaskStack
          println "processing $testTask"
          f.processTask( testTask)
      }

      def testUpdateTask(){
          def f = new FeedProcessor()

          def testTask= testTaskStack

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
