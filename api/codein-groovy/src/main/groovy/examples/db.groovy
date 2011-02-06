
import es.tid.socialcoding.dao.*

/*
class Schema implements Map
{
    def db
    Schema( String tablename){ init( tablename)}
    def init( String tablename ){
        db.eachRow("describe $tablename"){ it }
    }
}
*/

String logFileName= 'db.log'
System.setProperty("socialcoding.log.filename", logFileName)

i=0

//    dump( 'Entry')
//    dump2( 'Entry')

List l= getFields( 'Entry')

    l.each{ println it }

def getFields( String tablename)
{
def db= new DbHelper().db
    db.rows('describe '+tablename).collect{
       it.Field
    }
}


def dump (tablename){ 
def db= new DbHelper().db
    println " CONTENT OF TABLE ${tablename} ".center(40,'-') 
    db.query('describe '+tablename){ rs ->
        def meta = rs.metaData 
            if (meta.columnCount <= 0) return 
            for (i in 0..<meta.columnCount) {
                print "${i}: ${meta.getColumnLabel(i+1)}".padRight(20) 
                print "\n"
            } 
        println '-' * 40
    }
}

def transpose ( table) {
     return table.inject([:]){ mapa,row ->
         row.each{ column, value -> 
              if ( mapa?."$column"?.size() )
                  mapa."$column" += value
              else { mapa."$column"= new LinkedList( )       
                     mapa."$column"+= value
              }
         }
         mapa
    }
}

def dump2 (tablename){ 
def db= new DbHelper().db
    println " CONTENT OF TABLE ${tablename} ".center(40,'-') 
    db.eachRow('describe '+tablename){ println it }

def lista= db.rows('describe '+tablename)
    println " rows ".center( 40, '*')
    lista.each{ println it }
    m= transpose2( lista)
    println " Mapa transpuesto".center( 40, '-')
    m.each{ println it}
}

def transpose2( table){
    println "transpose2".center( 40, '-')
    mapa= [:]
    table[0].keySet().each{ mapa[ it] = table*."$it".collect{ it} }
    return mapa 
}

