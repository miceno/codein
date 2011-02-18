

class Super{

    Super(){
        println "super class: ${getClass().getName()}"
        println "super superclass: ${getClass().getSuperclass().getName()}"
    }
}

class Clase extends Super{
    Clase(){
        super()
        println "clase class: ${getClass().getName()}"
        println "clase superclass: ${getClass().getSuperclass().getName()}"
    }
}
        
new Super()
new Clase()
