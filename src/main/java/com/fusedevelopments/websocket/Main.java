package com.fusedevelopments.websocket;
import org.glassfish.tyrus.server.Server;

public class Main {
    public static void main(String[] args) {

        // Default values
        String hostName = "localhost";
        int port = 8081;
        String contextPath = "/";

        // Parsing command-line arguments
        for (String arg : args) {
            if (arg.startsWith("--hostName="))
                hostName = arg.split("=")[1];
            else if (arg.startsWith("--port="))
                port = Integer.parseInt(arg.split("=")[1]);
            else if (arg.startsWith("--contextPath="))
                contextPath = arg.split("=")[1];
        }

        // Create and start the server
        Server server = new Server(
                hostName,
                port,
                contextPath,
                null,
                WebSocketServer.class
        );

        try {
            server.start();
            System.out.print("Please press a key to stop the server.\n");
            System.in.read();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            server.stop();
        }
    }
}
