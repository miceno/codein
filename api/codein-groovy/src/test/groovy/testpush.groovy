

// 000 Valid service, valid class, valid token
def config000= {
baseurl : "http://localhost:8181/socialcoding/push"
payload : "probando=orestes" 
referer : "http://github.com" 
service : "github"
token   : "1234"
config  : { github: { className: "GithubPush", referrer: "http://github.com" }}
}

// 001 Valid service, valid class, invalid token
def config001= {
baseurl : "http://localhost:8181/socialcoding/push"
payload : "probando=orestes" 
referer : "http://github.com" 
service : "github"
token   : "2222"
config  : { github: { className: "GithubPush", referrer: "http://github.com" }}
}

// 010 Valid service, invalid class, valid token
def config010= {
baseurl : "http://localhost:8181/socialcoding/push"
payload : "probando=orestes" 
referer : "http://github.com" 
service : "github"
token   : "1234"
config  : { github: { className: "Github", referrer: "http://github.com" }}
}

// 011 Valid service, invalid class, invalid token
def config011= {
baseurl : "http://localhost:8181/socialcoding/push"
payload : "probando=orestes" 
referer : "http://github.com" 
service : "github"
token   : "2222"
config  : { github: { className: "Github", referrer: "http://github.com" }}
}


// 100 invalid service, valid class, valid token
def config100= {
baseurl : "http://localhost:8181/socialcoding/push"
payload : "probando=orestes" 
referer : "http://github.com" 
service : "mm"
token   : "1234"
config  : { github: { className: "GithubPush", referrer: "http://github.com" }}
}

// 101 invalid service, valid class, invalid token
def config101= {
baseurl : "http://localhost:8181/socialcoding/push"
payload : "probando=orestes" 
referer : "http://github.com" 
service : "kk"
token   : "2222"
config  : { github: { className: "GithubPush", referrer: "http://github.com" }}

// 110 invalid service, invalid class, valid token
def config110= {
baseurl : "http://localhost:8181/socialcoding/push"
payload : "probando=orestes" 
referer : "http://github.com" 
service : "kk"
token   : "1234"
config  : { github: { className: "Github", referrer: "http://github.com" }}
}

// 111 invalid service, invalid class, valid token
def config111= {
baseurl : "http://localhost:8181/socialcoding/push"
payload : "probando=orestes" 
referer : "http://github.com" 
service : "kk"
token   : "2222"
config  : { github: { className: "Github", referrer: "http://github.com" }}
}

