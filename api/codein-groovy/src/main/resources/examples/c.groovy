
def config = new ConfigSlurper().parse(new File('socialcoding.config').toURL())

   config.each{println it }


   config.root.resources_path="xxxx"

   config.each{println it }

