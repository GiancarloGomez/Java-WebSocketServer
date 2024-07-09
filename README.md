# Java WebSocket Server

This project is a simple Java WebSocket Server which supports namespaces and channels. 
This is a WIP at the moment as I learn how to work with [Eclipse Tyrus 2.1.5](https://projects.eclipse.org/projects/ee4j.tyrus/releases/2.1.5).

## Future Goals
Deploy as a module for [BoxLang](https://boxlang.io/)

## Running Server Via CLI
Browse to the releases folder and execute the following commands
```bash
cd releases/

# using defaults
java -jar WebSocketServer-1.0-SNAPSHOT-all.jar

# specifying arguments
java -jar WebSocketServerProject-1.0-SNAPSHOT-all.jar --hostName=localhost --port=8081 --contextPath=/ws  
```

## Sample Client
The examples folder contains a simple chat ui in the `html` folder that can be 
used to connect to the WebSocket server. The code uses the default arguments, 
so if you make any changes you will need to configure them in the `public/assets/app.js` file.
To run the example, you must have node installed and run the following command in the folder. 
Note, you can also run place on any WebServer you want and just point to your WebSocket Server. 
Again, this is a work in progress, so changes can be expected frequently.
```bash
cd examples/html
npm install
npm start
# browse to http://localhost:8080/
```
