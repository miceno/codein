
def start
def first
def second
def third
start = System.currentTimeMillis()

start = System.currentTimeMillis()
100000.times{
def s="hola soy orestes"
   s.toString()
}
first = System.currentTimeMillis() - start
println "first= $first"

start = System.currentTimeMillis()
100000.times{
def antonio="antonio"
def r="mi nombre es $antonio"
    r.toString()
}
second = System.currentTimeMillis() - start
println "second= $second"

start = System.currentTimeMillis()
100000.times{
def alberto="alberto"
def m="yo soy el " + alberto
    m.toString()
}
third= System.currentTimeMillis() - start
println "third= $third"
