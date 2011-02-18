
// New user
def crud=[
     method: "POST"
     test: "Create new user"
     target: "/socialcoding/user"
     UUID: "orestes"
     domain: "test-domain"
     token: "4444"
     urls: "http://www.tid.es|http://google.com"
]

// New user, identical UUID diferent domain
def crud=[
     method: "POST"
     test: "Create new user same UUID different domain"
     target: "/socialcoding/user"
     UUID: "orestes"
     domain: "new-domain"
     token: "4444"
     urls: "http://www.tid.es|http://google.com"
]

// New user, without domain
def crud=[
    method: "POST"
     test: "Create new user without domain"
     target: "/socialcoding/user"
     UUID: "orestes"
     domain: ""
     token: "4444"
     urls: "http://www.tid.es|http://google.com"
]

// New user, without UUID nor domain
def crud=[
    method: "POST"
     test: "Create new user without UUID nor domain"
     target: "/socialcoding/user"
     UUID: ""
     domain: ""
     token: "4444"
     urls: "http://www.tid.es|http://google.com"
]

// Update user
def crud=[
     test: "Update existing user"
     method: "POST"
     target: "/socialcoding/user"
     UUID: "orestes"
     domain: "test-domain"
     token: "4444"
     urls: "http://new.tid.es|http://terra.es"
]



// List all users 
def list=[
     test: "List all user"
     method: "GET"
     target: "/socialcoding/user"
     domain: ""
]

// List all users with pagination
def list=[
     test: "List all user offset 2, limit 4"
     method: "GET"
     target: "/socialcoding/user"
     domain: ""
     start: "2"
     size: "4"
]


// List empty domain 
def list=[
     test: "List empty domain"
     method: "GET"
     target: "/socialcoding/user"
     domain: "kk"
]

// List domain 
def list=[
     test: "List domain"
     method: "GET"
     target: "/socialcoding/user"
     domain: "test-domain"
]

// List domain with pagination
def list=[
     test: "List domain offset 2, limit 4"
     method: "GET"
     target: "/socialcoding/user"
     domain: "test-domain"
     start: "2"
     size: "4"
]

class UserTest{
    setup(){
        def         hostname="localhost" 
        def         port="8889" 
        def         database="testcodein" 
        // Options syntax is Form encoded: name1=value1&name2=value2... 
        def         options="autoReconnect=true" 
        def         url = "jdbc:mysql://$hostname:$port/$database?$options" 
        def         username = "root" 
        def         password = "root" 
        def         driver = "com.mysql.jdbc.Driver" 
        // Set jdbc configuration to point to a test database
        def db= Sql.newInstance( url,
    							username,
    							password,
    							driver)
        
    }
}
